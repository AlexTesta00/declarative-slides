package cli

import declslides.application.ApplicationError
import declslides.application.RenderFormat
import declslides.cli.CliCommand
import declslides.cli.CliParser
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliParserSpec extends AnyFlatSpec with Matchers:

  behavior of "CliParser"

  it should "default to help when no arguments are provided" in:
    CliParser.parse(Nil) shouldBe Right(CliCommand.Help)

  it should "parse the help command" in:
    CliParser.parse(List("help")) shouldBe Right(CliCommand.Help)

  it should "parse the list command" in:
    CliParser.parse(List("list")) shouldBe Right(CliCommand.ListPresentations)

  it should "parse the render command in html format" in:
    CliParser.parse(List("render", "demo", "html")) shouldBe
      Right(CliCommand.Render("demo", RenderFormat.Html))

  it should "parse the render command in text format" in:
    CliParser.parse(List("render", "demo", "text")) shouldBe
      Right(CliCommand.Render("demo", RenderFormat.Text))

  it should "accept txt as an alias for text" in:
    CliParser.parse(List("render", "demo", "txt")) shouldBe
      Right(CliCommand.Render("demo", RenderFormat.Text))

  it should "reject unsupported formats" in:
    val result = CliParser.parse(List("render", "demo", "pdf"))

    result shouldBe Left(
      ApplicationError.InvalidCommand(
        "Unsupported format 'pdf'. Expected one of: html, text",
      ),
    )

  it should "reject malformed render commands" in:
    val result = CliParser.parse(List("render"))

    result.isLeft shouldBe true

  it should "reject unknown commands" in:
    val result = CliParser.parse(List("open", "demo"))

    result.isLeft shouldBe true

  it should "reject extra arguments for list" in:
    val result = CliParser.parse(List("list", "extra"))

    result.isLeft shouldBe true
