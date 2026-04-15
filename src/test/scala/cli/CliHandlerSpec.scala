package cli

import declslides.application.FileSystem
import declslides.application.InMemoryPresentationRegistry
import declslides.application.PresentationRegistry
import declslides.application.RenderPresentation
import declslides.cli.CliCommand
import declslides.cli.CliHandler
import declslides.cli.OutputPort
import declslides.domain.DomainError
import declslides.domain.Presentation
import declslides.dsl.DSL._
import declslides.rendering.RenderingTarget.Html
import declslides.rendering.RenderingTarget.Text
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliHandlerSpec extends AnyFlatSpec with Matchers:

  private def validPresentation(
    result: Either[Vector[DomainError], Presentation],
  ): Presentation =
    result match
      case Right(presentation) => presentation
      case Left(errors) => fail(errors.map(_.message).mkString("; "))

  private val sampleDeck: Presentation =
    validPresentation(
      presentation("Demo"):
        deck(
          slide("Intro"):
            content(
              text("Hello"),
            ),
        ),
    )

  private final class RecordingOutput extends OutputPort:
    var lines: Vector[String] = Vector.empty

    override def writeLine(line: String): Unit =
      lines = lines :+ line

  private def handlerWith(out: RecordingOutput): CliHandler =
    val registry: PresentationRegistry =
      InMemoryPresentationRegistry("demo" -> sampleDeck)

    val renderPresentation =
      new RenderPresentation(
        registry = registry,
        htmlRenderer = new HtmlRenderer,
        textRenderer = new TextRenderer,
        fileSystem = FileSystem.noop,
      )

    new CliHandler(
      registry = registry,
      renderPresentation = renderPresentation,
      output = out,
    )

  "A CLI handler" should "print help text" in:
    val out = new RecordingOutput
    val handler = handlerWith(out)

    val exitCode = handler.handle(CliCommand.Help)

    exitCode shouldBe 0
    out.lines.exists(_.contains("Commands:")) shouldBe true

  it should "list available presentations" in:
    val out = new RecordingOutput
    val handler = handlerWith(out)

    val exitCode = handler.handle(CliCommand.ListPresentations)

    exitCode shouldBe 0
    out.lines should contain("Available presentations:")
    out.lines.exists(_.contains("demo")) shouldBe true

  it should "render a presentation to stdout in text format" in:
    val out = new RecordingOutput
    val handler = handlerWith(out)

    val exitCode = handler.handle(CliCommand.Render("demo", Text))

    exitCode shouldBe 0
    out.lines.exists(_.contains("Rendered 'demo' as text.")) shouldBe true
    out.lines.exists(_.contains("Demo")) shouldBe true
    out.lines.exists(_.contains("[1] Intro")) shouldBe true

  it should "render a presentation to stdout in html format" in:
    val out = new RecordingOutput
    val handler = handlerWith(out)

    val exitCode = handler.handle(CliCommand.Render("demo", Html))

    exitCode shouldBe 0
    out.lines.exists(_.contains("Rendered 'demo' as html.")) shouldBe true
    out.lines.exists(_.contains("<!DOCTYPE html>")) shouldBe true

  it should "return a non-zero exit code when rendering fails" in:
    val out = new RecordingOutput
    val handler = handlerWith(out)

    val exitCode = handler.handle(CliCommand.Render("missing", Text))

    exitCode shouldBe 1
    out.lines.exists(_.startsWith("Error:")) shouldBe true
