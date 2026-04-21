package cli

import declslides.cli.DeclSlidesCli
import declslides.cli.ExitCode
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DeclSlidesCliSpec extends AnyFlatSpec with Matchers:

  behavior of "DeclSlidesCli"

  it should
    "return a failure exit code and print usage when arguments are invalid" in:
      val printed = scala.collection.mutable.ListBuffer.empty[String]

      val exitCode =
        DeclSlidesCli.run(
          args = Array.empty,
          printError = printed += _,
        )

      exitCode shouldBe ExitCode.Failure.value
      printed.mkString("\n") should include("Usage:")
      printed.mkString("\n") should include("--input")
      printed.mkString("\n") should include("--format")
      printed.mkString("\n") should include("--output")

  it should "return a failure exit code and print an unknown option error" in {
    val printed = scala.collection.mutable.ListBuffer.empty[String]

    val exitCode =
      DeclSlidesCli.run(
        args = Array("--wat", "value"),
        printError = printed += _,
      )

    exitCode shouldBe ExitCode.Failure.value
    printed.mkString("\n") should include("Unknown option: --wat")
  }
