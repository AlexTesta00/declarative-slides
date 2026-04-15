package cli

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

  private def registry(entries: (String, Presentation)*) =
    InMemoryPresentationRegistry(entries*)

  private def handlerWith(output: RecordingOutput): CliHandler =
    val presentationRegistry: PresentationRegistry =
      registry(presentationName -> sampleDeck)

    val renderPresentation =
      new RenderPresentation(
        registry = presentationRegistry,
        rendererRegistry = rendererRegistry,
        fileSystem = FileSystem.noop,
      )

    new CliHandler(
      presentationRegistry = presentationRegistry,
      renderPresentation = renderPresentation,
      rendererRegistry = rendererRegistry,
      output = output,
    )

  private def outputAndHandler(): (RecordingOutput, CliHandler) =
    val output = new RecordingOutput
    (output, handlerWith(output))

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
      handler.handle(CliCommand.Render(presentationName, textFormat))

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
      handler.handle(CliCommand.Render(presentationName, htmlFormat))

    exitCode.shouldBe(Success)
    containsLine(
      output,
      CliMessages.renderSuccessMessage(presentationName, htmlFormat),
    ).shouldBe(true)
    containsFragment(output, "<!DOCTYPE html>").shouldBe(true)

  it should "return a non-zero exit code when rendering fails" in:
    val (output, handler) = outputAndHandler()

    val exitCode = handler.handle(CliCommand.Render("missing", textFormat))

    exitCode.shouldBe(Failure)
    containsLine(
      output,
      CliMessages.renderErrorMessage("Presentation 'missing' was not found"),
    ).shouldBe(true)
