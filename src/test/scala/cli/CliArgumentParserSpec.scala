package cli

import declslides.cli.CliArgumentParser
import declslides.cli.CliConfig
import declslides.cli.CliError
import declslides.cli.CliOption
import declslides.cli.OutputFormat
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliArgumentParserSpec extends AnyFlatSpec with Matchers:

  behavior of "CliArgumentParser"

  it should "parse valid arguments in the canonical order" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "--input",
          "slides.sc",
          "--format",
          "html",
          "--output",
          "slides.html",
        ),
      )

    result shouldBe Right(
      CliConfig(
        input = os.Path("slides.sc", os.pwd),
        format = OutputFormat.Html,
        output = os.Path("slides.html", os.pwd),
      ),
    )

  it should "parse valid arguments independently from their order" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "--format",
          "txt",
          "--output",
          "slides.txt",
          "--input",
          "slides.sc",
        ),
      )

    result shouldBe Right(
      CliConfig(
        input = os.Path("slides.sc", os.pwd),
        format = OutputFormat.Txt,
        output = os.Path("slides.txt", os.pwd),
      ),
    )

  it should "fail when a required option is missing" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "--format",
          "html",
          "--output",
          "slides.html",
        ),
      )

    result shouldBe Left(CliError.MissingOption(CliOption.Input))

  it should "fail when an option is repeated" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "--input",
          "slides.sc",
          "--input",
          "other.sc",
          "--format",
          "html",
          "--output",
          "slides.html",
        ),
      )

    result shouldBe Left(CliError.DuplicateOption(CliOption.Input))

  it should "fail when an option is unknown" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "--input",
          "slides.sc",
          "--theme",
          "dark",
          "--format",
          "html",
          "--output",
          "slides.html",
        ),
      )

    result shouldBe Left(CliError.UnknownOption("--theme"))

  it should "fail when an option has no value" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "--input",
          "slides.sc",
          "--format",
          "html",
          "--output",
        ),
      )

    result shouldBe Left(CliError.MissingValue("--output"))

  it should "fail when the format is unsupported" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "--input",
          "slides.sc",
          "--format",
          "pdf",
          "--output",
          "slides.pdf",
        ),
      )

    result shouldBe Left(
      CliError.InvalidFormat(
        provided = "pdf",
        supported = OutputFormat.supportedValues,
      ),
    )

  it should "fail when a positional argument is encountered" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "slides.sc",
          "--format",
          "html",
          "--output",
          "slides.html",
        ),
      )

    result shouldBe Left(CliError.UnexpectedArgument("slides.sc"))

  it should "parse markdown as a supported output format" in:
    val result =
      CliArgumentParser.parse(
        Array(
          "--input",
          "slides.sc",
          "--format",
          "markdown",
          "--output",
          "slides.md",
        ),
      )

    result shouldBe Right(
      CliConfig(
        input = os.Path("slides.sc", os.pwd),
        format = OutputFormat.Markdown,
        output = os.Path("slides.md", os.pwd),
      ),
    )
