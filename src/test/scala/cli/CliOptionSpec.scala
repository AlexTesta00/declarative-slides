package cli

import declslides.cli.CliError
import declslides.cli.CliOption
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliOptionSpec extends AnyFlatSpec with Matchers:

  behavior of "CliOption"

  it should "expose all supported options explicitly" in:
    CliOption.all shouldBe List(
      CliOption.Input,
      CliOption.Format,
      CliOption.Output,
    )

  it should "parse the input option" in:
    CliOption.parse("--input") shouldBe Right(CliOption.Input)

  it should "parse the format option" in:
    CliOption.parse("--format") shouldBe Right(CliOption.Format)

  it should "parse the output option" in:
    CliOption.parse("--output") shouldBe Right(CliOption.Output)

  it should "fail on unknown options" in:
    CliOption.parse("--verbose") shouldBe
      Left(CliError.UnknownOption("--verbose"))
