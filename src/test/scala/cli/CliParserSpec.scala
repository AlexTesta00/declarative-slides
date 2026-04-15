package cli

import declslides.application.ApplicationError
import declslides.cli.CliCommand
import declslides.cli.CliParser
import declslides.rendering.RenderingTarget
import declslides.rendering.RenderingTarget.Html
import declslides.rendering.RenderingTarget.Text
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliParserSpec extends AnyFlatSpec with Matchers:

  behavior of "CliParser"

  private def parse(args: String*) =
    CliParser.parse(args.toList)

  private val supportedFormats =
    RenderingTarget.supportedLabels.mkString("|")

  private val usageError =
    Left(
      ApplicationError.InvalidCommand(
        s"Usage: list | help | render <presentation-name> <$supportedFormats>",
      ),
    )

  private def unsupportedFormatError(raw: String) =
    Left(
      ApplicationError.InvalidCommand(
        s"Unsupported format '$raw'. Expected one of: ${RenderingTarget.supportedLabels.mkString(", ")}",
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
      Right(CliCommand.Render("demo", Html)),
    )

  it should "parse the render command in text format" in:
    parse("render", "demo", "text").shouldBe(
      Right(CliCommand.Render("demo", Text)),
    )

  it should "accept txt as an alias for text" in:
    parse("render", "demo", "txt").shouldBe(
      Right(CliCommand.Render("demo", Text)),
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
