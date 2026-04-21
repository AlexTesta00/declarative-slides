package cli

import declslides.application.ApplicationError
import declslides.application.RenderCommand
import declslides.application.ScriptRunner
import declslides.cli.CliError
import declslides.cli.CliProgram
import declslides.cli.ExitCode
import declslides.cli.RenderCommandFactory
import declslides.rendering.DefaultRendererRegistry
import declslides.rendering.RenderFormat
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliProgramSpec extends AnyFlatSpec with Matchers:

  behavior of "CliProgram"

  it should "fail before creating the command when arguments are invalid" in:
    val printedErrors = scala.collection.mutable.ListBuffer.empty[String]
    val printedInfo = scala.collection.mutable.ListBuffer.empty[String]

    val factory =
      new CountingFactory(CliError.RuntimeInitialization("should not be used"))

    val program =
      new CliProgram(
        commandFactory = factory,
        printError = printedErrors += _,
        printInfo = printedInfo += _,
      )

    val exitCode =
      program.run(Array.empty)

    exitCode shouldBe ExitCode.Failure
    factory.invocationCount shouldBe 0
    printedErrors.mkString("\n") should include("Usage:")
    printedInfo shouldBe empty

  it should "return a failure exit code when the command factory fails" in:
    val printedErrors = scala.collection.mutable.ListBuffer.empty[String]
    val printedInfo = scala.collection.mutable.ListBuffer.empty[String]

    val factory =
      new CountingFactory(CliError.RuntimeInitialization("scala-cli not found"))

    val program =
      new CliProgram(
        commandFactory = factory,
        printError = printedErrors += _,
        printInfo = printedInfo += _,
      )

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
    printedErrors.mkString("\n") should include("scala-cli not found")
    printedInfo shouldBe empty

  it should "print a confirmation message when rendering succeeds" in:
    val printedErrors = scala.collection.mutable.ListBuffer.empty[String]
    val printedInfo = scala.collection.mutable.ListBuffer.empty[String]

    val factory =
      new SuccessfulFactory

    val program =
      new CliProgram(
        commandFactory = factory,
        printError = printedErrors += _,
        printInfo = printedInfo += _,
      )

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

    exitCode shouldBe ExitCode.Success
    printedErrors shouldBe empty
    printedInfo.size shouldBe 1
    printedInfo.head should include("out.html")

private final class CountingFactory(
  error: CliError) extends RenderCommandFactory:

  var invocationCount: Int = 0

  override def create(): Either[CliError, RenderCommand] =
    invocationCount += 1
    Left(error)

private final class SuccessfulFactory extends RenderCommandFactory:

  override def create(): Either[CliError, RenderCommand] =
    Right(
      new RenderCommand(
        registry = DefaultRendererRegistry.live,
        runner = SuccessfulRunner,
      ),
    )

private object SuccessfulRunner extends ScriptRunner:

  override def render(
    input: os.Path,
    target: RenderFormat,
    output: os.Path,
  ): Either[ApplicationError, Unit] =
    Right(())
