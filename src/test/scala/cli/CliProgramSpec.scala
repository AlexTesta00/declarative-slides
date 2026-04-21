package cli

import declslides.application.RenderCommand
import declslides.cli.CliError
import declslides.cli.CliProgram
import declslides.cli.ExitCode
import declslides.cli.RenderCommandFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliProgramSpec extends AnyFlatSpec with Matchers:

  behavior of "CliProgram"

  it should "fail before creating the command when arguments are invalid" in:
    val printed = scala.collection.mutable.ListBuffer.empty[String]
    val factory =
      new CountingFactory(CliError.RuntimeInitialization("should not be used"))
    val program = new CliProgram(factory, printed += _)

    val exitCode =
      program.run(Array.empty)

    exitCode shouldBe ExitCode.Failure
    factory.invocationCount shouldBe 0
    printed.mkString("\n") should include("Usage:")

  it should "return a failure exit code when the command factory fails" in:
    val printed = scala.collection.mutable.ListBuffer.empty[String]
    val factory =
      new CountingFactory(CliError.RuntimeInitialization("scala-cli not found"))
    val program = new CliProgram(factory, printed += _)

    val exitCode =
      program.run(
        Array(
          "--input",
          "deck.sc",
          "--format",
          "html",
          "--output",
          "out.html",
        ),
      )

    exitCode shouldBe ExitCode.Failure
    factory.invocationCount shouldBe 1
    printed.mkString("\n") should include("scala-cli not found")

private final class CountingFactory(
  error: CliError) extends RenderCommandFactory:

  var invocationCount: Int = 0

  override def create(): Either[CliError, RenderCommand] =
    invocationCount += 1
    Left(error)
