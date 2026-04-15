package declslides

import declslides.application.ApplicationError
import declslides.application.FileSystem
import declslides.application.InMemoryPresentationRegistry
import declslides.application.RenderPresentation
import declslides.application.RenderRequest
import declslides.domain.DomainError
import declslides.domain.Layout
import declslides.domain.Presentation
import declslides.domain.Theme
import declslides.dsl.DSL._
import declslides.rendering.RenderingTarget.Html
import declslides.rendering.RenderingTarget.Text
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

object Main:

  private object LocalFileSystem extends FileSystem:

    override def write(
      path: String,
      content: String,
    ): Either[ApplicationError, Unit] =
      try
        val outputPath = Paths.get(path)

        Option(outputPath.getParent).foreach(Files.createDirectories(_))

        Files.writeString(outputPath, content, StandardCharsets.UTF_8)
        Right(())
      catch
        case exception: Exception =>
          Left(
            ApplicationError.WriteFailure(
              path,
              Option(
                exception.getMessage,
              ).getOrElse(exception.getClass.getSimpleName),
            ),
          )

  private def presentationErrorMessage(errors: Vector[DomainError]): String =
    errors.map(_.message).mkString("; ")

  private def buildPresentation(): Either[Vector[DomainError], Presentation] =
    presentation("My Declarative Slides") {
      deck(
        theme(Theme.default),

        slide("Introduction", Layout.Centered) {
          content(
            text("A presentation written directly inside Main.scala"),
            bullets("Scala 3", "DSL embedded", "HTML rendering"),
          )
        },

        slide("Code Example") {
          content(
            text("A simple Scala example:"),
            code(
              "scala",
              """val x = 42
                |println(x)""".stripMargin,
            ),
          )
        },

        slide("Closing") {
          content(
            text("This HTML file can be opened in the browser."),
            spacer(),
            text("End of demo."),
          )
        },
      )
    }

  private def buildRenderPresentation(
    presentation: Presentation,
  ): RenderPresentation =
    new RenderPresentation(
      registry = InMemoryPresentationRegistry(
        "my-presentation" -> presentation,
      ),
      htmlRenderer = new HtmlRenderer,
      textRenderer = new TextRenderer,
      fileSystem = LocalFileSystem,
    )

  private def renderAndReport(
    service: RenderPresentation,
    request: RenderRequest,
    label: String,
  ): Unit =
    service.run(request) match
      case Right(result) =>
        println(
          s"$label generated in: ${result.writtenTo.getOrElse("unknown path")}",
        )

      case Left(error) =>
        println(s"$label error: ${error.message}")

  @main def renderMyPresentation(): Unit =
    buildPresentation() match
      case Left(errors) =>
        println(s"Invalid presentation: ${presentationErrorMessage(errors)}")

      case Right(presentation) =>
        val service = buildRenderPresentation(presentation)

        renderAndReport(
          service,
          RenderRequest(
            presentationName = "my-presentation",
            format = Html,
            outputPath = Some("out/my-presentation.html"),
          ),
          "HTML",
        )

        renderAndReport(
          service,
          RenderRequest(
            presentationName = "my-presentation",
            format = Text,
            outputPath = Some("out/my-presentation.txt"),
          ),
          "Text",
        )
