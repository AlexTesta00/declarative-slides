package cli

import declslides.cli.CliUsage
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliUsageSpec extends AnyFlatSpec with Matchers:

  behavior of "CliUsage"

  it should "describe the supported command line shape" in:
    CliUsage.text should include("Usage:")
    CliUsage.text should include("declslides")
    CliUsage.text should include("--input")
    CliUsage.text should include("--format")
    CliUsage.text should include("--output")

  it should "mention all supported output formats" in:
    CliUsage.text should include("html")
    CliUsage.text should include("text")
    CliUsage.text should include("txt")
    CliUsage.text should include("markdown")
    CliUsage.text should include("md")
