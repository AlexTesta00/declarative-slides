package cli

import declslides.application.FileSystem
import declslides.application.InMemoryPresentationRegistry
import declslides.application.PresentationRegistry
import declslides.application.RenderPresentation
import declslides.cli.CliCommand
import declslides.cli.CliExitCode.Failure
import declslides.cli.CliExitCode.Success
import declslides.cli.CliHandler
import declslides.cli.OutputPort
import declslides.domain.Presentation
import declslides.dsl.DSL._
import declslides.rendering.RenderingTarget.Html
import declslides.rendering.RenderingTarget.Text
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliHandlerSpec extends AnyFlatSpec with Matchers:

  behavior of "CliHandler"

  private val sampleDeck: Presentation =
    presentation("Demo"):
      deck(
        slide("Intro"):
          content(
            text("Hello"),
          ),
      )
    .value

  private final class RecordingOutput extends OutputPort:
    var lines: Vector[String] = Vector.empty

    override def writeLine(line: String): Unit =
      lines = lines :+ line

  private def registry(entries: (String, Presentation)*) =
    InMemoryPresentationRegistry(entries*)

  private def handlerWith(output: RecordingOutput): CliHandler =
    val presentationRegistry: PresentationRegistry =
      registry("demo" -> sampleDeck)

    val renderPresentation =
      new RenderPresentation(
        registry = presentationRegistry,
        htmlRenderer = new HtmlRenderer,
        textRenderer = new TextRenderer,
        fileSystem = FileSystem.noop,
      )

    new CliHandler(
      registry = presentationRegistry,
      renderPresentation = renderPresentation,
      output = output,
    )

  it should "print help text" in:
    val output = new RecordingOutput
    val handler = handlerWith(output)

    val exitCode = handler.handle(CliCommand.Help)

    exitCode.shouldBe(Success)
    output.lines.exists(_.contains("Commands:")).shouldBe(true)

  it should "list available presentations" in:
    val output = new RecordingOutput
    val handler = handlerWith(output)

    val exitCode = handler.handle(CliCommand.ListPresentations)

    exitCode.shouldBe(Success)
    output.lines.should(contain("Available presentations:"))
    output.lines.exists(_.contains("demo")).shouldBe(true)

  it should "render a presentation to stdout in text format" in:
    val output = new RecordingOutput
    val handler = handlerWith(output)

    val exitCode = handler.handle(CliCommand.Render("demo", Text))

    exitCode.shouldBe(Success)
    output.lines.exists(_.contains("Rendered 'demo' as text.")).shouldBe(true)
    output.lines.exists(_.contains("Demo")).shouldBe(true)
    output.lines.exists(_.contains("[1] Intro")).shouldBe(true)

  it should "render a presentation to stdout in html format" in:
    val output = new RecordingOutput
    val handler = handlerWith(output)

    val exitCode = handler.handle(CliCommand.Render("demo", Html))

    exitCode.shouldBe(Success)
    output.lines.exists(_.contains("Rendered 'demo' as html.")).shouldBe(true)
    output.lines.exists(_.contains("<!DOCTYPE html>")).shouldBe(true)

  it should "return a non-zero exit code when rendering fails" in:
    val output = new RecordingOutput
    val handler = handlerWith(output)

    val exitCode = handler.handle(CliCommand.Render("missing", Text))

    exitCode.shouldBe(Failure)
    output.lines.exists(_.startsWith("Error:")).shouldBe(true)
