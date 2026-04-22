package cli

import declslides.cli.CliError
import declslides.cli.CliOption
import declslides.cli.OutputFormat
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliErrorSpec extends AnyFlatSpec with Matchers:

  behavior of "CliError"

  it should "describe a missing option together with usage" in:
    val error = CliError.MissingOption(CliOption.Input)

    error.message should include("Missing required option: --input")
    error.message should include("Usage:")

  it should "describe a missing value together with usage" in:
    val error = CliError.MissingValue("--output")

    error.message should include("Missing value for option: --output")
    error.message should include("Usage:")

  it should "describe an unknown option together with usage" in:
    val error = CliError.UnknownOption("--theme")

    error.message should include("Unknown option: --theme")
    error.message should include("Usage:")

  it should "describe a duplicate option explicitly" in:
    val error = CliError.DuplicateOption(CliOption.Format)

    error.message should include("Option specified more than once: --format")

  it should "describe an unexpected argument together with usage" in:
    val error = CliError.UnexpectedArgument("slides.sc")

    error.message should include("Unexpected argument: slides.sc")
    error.message should include("Usage:")

  it should "describe an invalid format together with the supported values" in:
    val error =
      CliError.InvalidFormat(
        provided = "pdf",
        supported = OutputFormat.supportedValues,
      )

    error.message should include("Unsupported format: pdf")
    error.message should include("html")
    error.message should include("text")
    error.message should include("txt")

  it should "preserve runtime initialization details verbatim" in:
    val error = CliError.RuntimeInitialization("scala-cli not found")

    error.message shouldBe "scala-cli not found"

  it should "preserve render failure details verbatim" in:
    val error = CliError.RenderFailure("render failed")

    error.message shouldBe "render failed"
