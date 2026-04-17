package cli

import declslides.application.ApplicationError
import declslides.application.FileSystem
import declslides.application.InMemoryPresentationRegistry
import declslides.application.PresentationRegistry
import declslides.application.RenderPresentation
import declslides.cli.CliCommand
import declslides.cli.CliExitCode.Failure
import declslides.cli.CliExitCode.Success
import declslides.cli.CliHandler
import declslides.cli.CliMessages
import declslides.cli.OutputPort
import declslides.domain.Presentation
import declslides.dsl.DSL._
import declslides.rendering.RendererRegistry
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliHandlerSpec extends AnyFlatSpec with Matchers:

  behavior of "CliHandler"

  private val presentationName = "demo"
  private val htmlOutputPath = "out/demo.html"
  private val textOutputPath = "out/demo.txt"

  private val sampleDeck: Presentation =
    presentation("Demo"):
      deck(
        slide("Intro"):
          content(
            text("Hello"),
          ),
      )
    .value

  private val rendererRegistry =
    RendererRegistry(
      new HtmlRenderer,
      new TextRenderer,
    )

  private val htmlFormat = HtmlRenderer.Target
  private val textFormat = TextRenderer.Target

  private final class RecordingOutput extends OutputPort:
    var lines: Vector[String] = Vector.empty

    override def writeLine(line: String): Unit =
      lines = lines :+ line

  private final class RecordingFileSystem(
    failureReason: Option[String] = None) extends FileSystem:

    var writes: Map[String, String] = Map.empty

    override def write(
      path: String,
      content: String,
    ): Either[ApplicationError, Unit] =
      failureReason match
        case Some(reason) =>
          Left(ApplicationError.WriteFailure(path, reason))
        case None =>
          writes = writes.updated(path, content)
          Right(())

  private def registry(entries: (String, Presentation)*) =
    InMemoryPresentationRegistry(entries*)

  private def handlerWith(
    output: RecordingOutput,
    fileSystem: FileSystem = FileSystem.noop,
  ): CliHandler =
    val presentationRegistry: PresentationRegistry =
      registry(presentationName -> sampleDeck)

    val renderPresentation =
      new RenderPresentation(
        registry = presentationRegistry,
        rendererRegistry = rendererRegistry,
        fileSystem = fileSystem,
      )

    new CliHandler(
      presentationRegistry = presentationRegistry,
      renderPresentation = renderPresentation,
      rendererRegistry = rendererRegistry,
      output = output,
    )

  private def outputAndHandler(
    fileSystem: FileSystem = FileSystem.noop,
  ): (RecordingOutput, CliHandler) =
    val output = new RecordingOutput
    (output, handlerWith(output, fileSystem))

  private def outputHandlerAndFileSystem(
    failureReason: Option[String] = None,
  ): (RecordingOutput, CliHandler, RecordingFileSystem) =
    val output = new RecordingOutput
    val fileSystem = new RecordingFileSystem(failureReason)
    (output, handlerWith(output, fileSystem), fileSystem)

  private def containsLine(
    output: RecordingOutput,
    value: String,
  ): Boolean =
    output.lines.contains(value)

  private def containsFragment(
    output: RecordingOutput,
    value: String,
  ): Boolean =
    output.lines.exists(_.contains(value))

  it should "print help text" in:
    val (output, handler) = outputAndHandler()

    val exitCode = handler.handle(CliCommand.Help)

    exitCode.shouldBe(Success)
    containsLine(output, CliMessages.helpText(rendererRegistry)).shouldBe(true)

  it should "list available presentations" in:
    val (output, handler) = outputAndHandler()

    val exitCode = handler.handle(CliCommand.ListPresentations)

    exitCode.shouldBe(Success)
    containsLine(
      output,
      CliMessages.availablePresentationsHeader,
    ).shouldBe(true)
    containsLine(
      output,
      s"${CliMessages.presentationBulletPrefix}$presentationName",
    ).shouldBe(true)

  it should "render a presentation to stdout in text format" in:
    val (output, handler) = outputAndHandler()

    val exitCode =
      handler.handle(CliCommand.Render(presentationName, textFormat, None))

    exitCode.shouldBe(Success)
    containsLine(
      output,
      CliMessages.renderSuccessMessage(presentationName, textFormat),
    ).shouldBe(true)
    containsFragment(output, sampleDeck.title).shouldBe(true)
    containsFragment(output, "[1] Intro").shouldBe(true)

  it should "render a presentation to stdout in html format" in:
    val (output, handler) = outputAndHandler()

    val exitCode =
      handler.handle(CliCommand.Render(presentationName, htmlFormat, None))

    exitCode.shouldBe(Success)
    containsLine(
      output,
      CliMessages.renderSuccessMessage(presentationName, htmlFormat),
    ).shouldBe(true)
    containsFragment(output, "<!DOCTYPE html>").shouldBe(true)

  it should
    "write a rendered text document to file when an output path is provided" in:
      val (output, handler, fileSystem) =
        outputHandlerAndFileSystem()

      val exitCode =
        handler.handle(
          CliCommand.Render(presentationName, textFormat, Some(textOutputPath)),
        )

      exitCode.shouldBe(Success)
      containsLine(
        output,
        CliMessages.renderSuccessMessage(presentationName, textFormat),
      ).shouldBe(true)
      containsLine(
        output,
        CliMessages.renderWrittenMessage(textOutputPath),
      ).shouldBe(true)
      containsFragment(output, "[1] Intro").shouldBe(false)
      fileSystem.writes.keySet.should(contain(textOutputPath))
      fileSystem.writes(textOutputPath).should(include("[1] Intro"))

  it should
    "write a rendered html document to file when an output path is provided" in:
      val (output, handler, fileSystem) =
        outputHandlerAndFileSystem()

      val exitCode =
        handler.handle(
          CliCommand.Render(presentationName, htmlFormat, Some(htmlOutputPath)),
        )

      exitCode.shouldBe(Success)
      containsLine(
        output,
        CliMessages.renderSuccessMessage(presentationName, htmlFormat),
      ).shouldBe(true)
      containsLine(
        output,
        CliMessages.renderWrittenMessage(htmlOutputPath),
      ).shouldBe(true)
      containsFragment(output, "<!DOCTYPE html>").shouldBe(false)
      fileSystem.writes.keySet.should(contain(htmlOutputPath))
      fileSystem.writes(htmlOutputPath).should(include("<!DOCTYPE html>"))

  it should
    "return a non-zero exit code when rendering fails because the presentation is missing" in:
      val (output, handler) = outputAndHandler()

      val exitCode =
        handler.handle(CliCommand.Render("missing", textFormat, None))

      exitCode.shouldBe(Failure)
      containsLine(
        output,
        CliMessages.renderErrorMessage(
          ApplicationError.PresentationNotFound("missing").message,
        ),
      ).shouldBe(true)

  it should
    "return a non-zero exit code when writing the rendered output fails" in:
      val failureReason = "disk full"
      val (output, handler, _) =
        outputHandlerAndFileSystem(Some(failureReason))

      val exitCode =
        handler.handle(
          CliCommand.Render(presentationName, textFormat, Some(textOutputPath)),
        )

      exitCode.shouldBe(Failure)
      containsLine(
        output,
        CliMessages.renderErrorMessage(
          ApplicationError.WriteFailure(textOutputPath, failureReason).message,
        ),
      ).shouldBe(true)
