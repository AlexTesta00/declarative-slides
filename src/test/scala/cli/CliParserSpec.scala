package cli

import declslides.application.ApplicationError
import declslides.cli.CliCommand
import declslides.cli.CliParser
import declslides.rendering.RendererRegistry
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliParserSpec extends AnyFlatSpec with Matchers:

  behavior of "CliParser"

  private val rendererRegistry =
    RendererRegistry(
      new HtmlRenderer,
      new TextRenderer,
    )

  private val parser =
    new CliParser(rendererRegistry)

  private val htmlFormat = HtmlRenderer.Target
  private val textFormat = TextRenderer.Target

  private def parse(args: String*) =
    parser.parse(args.toList)

  private val supportedFormats =
    rendererRegistry.supportedLabels.mkString("|")

  private val usageError =
    Left(
      ApplicationError.InvalidCommand(
        s"Usage: list | help | render <presentation-name> <$supportedFormats>",
      ),
    )

  private def unsupportedFormatError(raw: String) =
    Left(
      ApplicationError.InvalidCommand(
        s"Unsupported format '$raw'. Expected one of: ${rendererRegistry.supportedLabels.mkString(", ")}",
      ),
    )

  it should "default to help when no arguments are provided" in:
    parse().shouldBe(Right(CliCommand.Help))

  it should "parse the help command" in:
    parse("help").shouldBe(Right(CliCommand.Help))

  it should "parse the list command" in:
    parse("list").shouldBe(Right(CliCommand.ListPresentations))

  it should "parse the render command in html format" in:
    parse("render", "demo", "html").shouldBe(
      Right(CliCommand.Render("demo", htmlFormat)),
    )

  it should "parse the render command in text format" in:
    parse("render", "demo", "text").shouldBe(
      Right(CliCommand.Render("demo", textFormat)),
    )

  it should "accept txt as an alias for text" in:
    parse("render", "demo", "txt").shouldBe(
      Right(CliCommand.Render("demo", textFormat)),
    )

  it should "reject unsupported formats" in:
    parse("render", "demo", "pdf").shouldBe(
      unsupportedFormatError("pdf"),
    )

  it should "reject malformed render commands" in:
    parse("render").shouldBe(usageError)

  it should "reject unknown commands" in:
    parse("open", "demo").shouldBe(usageError)

  it should "reject extra arguments for list" in:
    parse("list", "extra").shouldBe(usageError)
