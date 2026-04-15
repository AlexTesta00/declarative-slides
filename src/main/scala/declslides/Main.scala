package declslides

import declslides.application.ApplicationError
import declslides.application.FileSystem
import declslides.application.InMemoryPresentationRegistry
import declslides.application.PresentationRegistry
import declslides.application.RenderPresentation
import declslides.cli.CliExitCode
import declslides.cli.CliHandler
import declslides.cli.CliMessages
import declslides.cli.CliParser
import declslides.cli.OutputPort
import declslides.cli.StdOutput
import declslides.domain.DomainError
import declslides.domain.Layout
import declslides.domain.Presentation
import declslides.domain.Theme
import declslides.dsl.DSL._
import declslides.rendering.RendererRegistry
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

object Main:

  private val presentationName = "my-presentation"

  private object LocalFileSystem extends FileSystem:

    override def write(
      path: String,
      content: String,
    ): Either[ApplicationError, Unit] =
      try
        val outputPath = Paths.get(path)

        Option(outputPath.getParent)
          .foreach(parent => Files.createDirectories(parent))

        Files.writeString(outputPath, content, StandardCharsets.UTF_8)
        Right(())
      catch
        case exception: Exception =>
          Left(
            ApplicationError.WriteFailure(
              path,
              Option(exception.getMessage)
                .getOrElse(exception.getClass.getSimpleName),
            ),
          )

  private def domainErrorsMessage(errors: Vector[DomainError]): String =
    errors.map(_.message).mkString("; ")

  private def buildPresentation(): Either[Vector[DomainError], Presentation] =
    presentation("My Declarative Slides"):
      deck(
        theme(Theme.default),

        slide("Introduction", Layout.Centered):
          content(
            text("A presentation written directly inside Main.scala"),
            bullets("Scala 3", "DSL embedded", "HTML rendering"),
          )
        ,

        slide("Code Example"):
          content(
            text("A simple Scala example:"),
            code(
              "scala",
              """val x = 42
                |println(x)""".stripMargin,
            ),
          )
        ,

        slide("Closing"):
          content(
            text("This output can be rendered from the CLI."),
            spacer(),
            text("End of demo."),
          ),
      )

  private def buildPresentationRegistry(
    presentation: Presentation,
  ): PresentationRegistry =
    InMemoryPresentationRegistry(
      presentationName -> presentation,
    )

  private def buildRendererRegistry(): RendererRegistry =
    RendererRegistry(
      new HtmlRenderer,
      new TextRenderer,
    )

  private def buildRenderPresentation(
    presentationRegistry: PresentationRegistry,
    rendererRegistry: RendererRegistry,
  ): RenderPresentation =
    new RenderPresentation(
      registry = presentationRegistry,
      rendererRegistry = rendererRegistry,
      fileSystem = LocalFileSystem,
    )

  private def buildCliHandler(
    presentationRegistry: PresentationRegistry,
    rendererRegistry: RendererRegistry,
    output: OutputPort,
  ): CliHandler =
    new CliHandler(
      presentationRegistry = presentationRegistry,
      renderPresentation = buildRenderPresentation(
        presentationRegistry,
        rendererRegistry,
      ),
      rendererRegistry = rendererRegistry,
      output = output,
    )

  private def run(
    args: List[String],
    output: OutputPort = StdOutput,
  ): Int =
    buildPresentation() match
      case Left(errors) =>
        output.writeLine(
          CliMessages.renderErrorMessage(
            s"Invalid presentation: ${domainErrorsMessage(errors)}",
          ),
        )
        CliExitCode.Failure

      case Right(presentation) =>
        val presentationRegistry = buildPresentationRegistry(presentation)
        val rendererRegistry = buildRendererRegistry()
        val parser = new CliParser(rendererRegistry)
        val handler = buildCliHandler(
          presentationRegistry,
          rendererRegistry,
          output,
        )

        parser.parse(args) match
          case Left(error) =>
            output.writeLine(CliMessages.renderErrorMessage(error.message))
            CliExitCode.Failure

          case Right(command) =>
            handler.handle(command)

  @main def declslides(args: String*): Unit =
    val exitCode = run(args.toList)

    if exitCode != CliExitCode.Success then
      sys.exit(exitCode)
