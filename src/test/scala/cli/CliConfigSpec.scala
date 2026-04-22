package cli

import declslides.cli.CliConfig
import declslides.cli.OutputFormat
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliConfigSpec extends AnyFlatSpec with Matchers:

  behavior of "CliConfig"

  it should "store the input path, output format and output path explicitly" in:
    val config =
      CliConfig(
        input = os.Path("deck.sc", os.pwd),
        format = OutputFormat.Text,
        output = os.Path("deck.txt", os.pwd),
      )

    config.input shouldBe os.Path("deck.sc", os.pwd)
    config.format shouldBe OutputFormat.Text
    config.output shouldBe os.Path("deck.txt", os.pwd)

  it should "support value equality as a domain configuration object" in:
    val left =
      CliConfig(
        input = os.Path("deck.sc", os.pwd),
        format = OutputFormat.Html,
        output = os.Path("deck.html", os.pwd),
      )

    val right =
      CliConfig(
        input = os.Path("deck.sc", os.pwd),
        format = OutputFormat.Html,
        output = os.Path("deck.html", os.pwd),
      )

    left shouldBe right
