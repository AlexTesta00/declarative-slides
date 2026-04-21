package cli

import declslides.cli.ExitCode
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExitCodeSpec extends AnyFlatSpec with Matchers:

  behavior of "ExitCode"

  it should "define success as zero" in:
    ExitCode.Success.value shouldBe 0

  it should "define failure as one" in:
    ExitCode.Failure.value shouldBe 1

  it should "use different numeric values for each exit code" in:
    ExitCode.Success.value should not be ExitCode.Failure.value
