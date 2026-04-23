package cli

import declslides.cli.CliError
import declslides.cli.OutputFormat
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OutputFormatSpec extends AnyFlatSpec with Matchers:

  behavior of "OutputFormat"

  it should "expose all supported formats explicitly" in:
    OutputFormat.supported shouldBe List(
      OutputFormat.Html,
      OutputFormat.Text,
      OutputFormat.Txt,
      OutputFormat.Markdown,
      OutputFormat.Md,
    )

  it should "parse html" in:
    OutputFormat.parse("html") shouldBe Right(OutputFormat.Html)

  it should "parse text" in:
    OutputFormat.parse("text") shouldBe Right(OutputFormat.Text)

  it should "parse txt" in:
    OutputFormat.parse("txt") shouldBe Right(OutputFormat.Txt)

  it should "parse markdown" in:
    OutputFormat.parse("markdown") shouldBe Right(OutputFormat.Markdown)

  it should "parse md" in:
    OutputFormat.parse("md") shouldBe Right(OutputFormat.Md)

  it should "expose supported values as plain strings" in:
    OutputFormat.supportedValues shouldBe
      List("html", "text", "txt", "markdown", "md")

  it should "fail on unsupported formats" in:
    OutputFormat.parse("pdf") shouldBe Left(
      CliError.InvalidFormat(
        provided = "pdf",
        supported = OutputFormat.supportedValues,
      ),
    )
