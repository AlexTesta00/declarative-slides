package application

import declslides.application.ApplicationError
import declslides.application.RenderCommand
import declslides.application.ScriptRunner
import declslides.rendering.DefaultRendererRegistry
import declslides.rendering.RenderFormat
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RenderCommandSpec extends AnyFlatSpec with Matchers:

  behavior of "RenderCommand"

  it should "fail when the requested format is unsupported" in:
    val runner = new RecordingScriptRunner(Right(()))
    val command =
      new RenderCommand(
        registry = DefaultRendererRegistry.live,
        runner = runner,
      )

    val result =
      command.run(
        input = os.Path("deck.sc", os.pwd),
        format = "pdf",
        output = os.Path("deck.out", os.pwd),
      )

    result shouldBe Left(
      ApplicationError.UnsupportedFormat(
        raw = "pdf",
        supported = DefaultRendererRegistry.live.supportedLabels,
      ),
    )
    runner.invocationCount shouldBe 0

  it should
    "delegate rendering to the script runner when the format is supported" in:
      val runner = new RecordingScriptRunner(Right(()))
      val command =
        new RenderCommand(
          registry = DefaultRendererRegistry.live,
          runner = runner,
        )

      val input = os.Path("deck.sc", os.pwd)
      val output = os.Path("deck.out", os.pwd)
      val format = DefaultRendererRegistry.live.supportedLabels.head

      val result =
        command.run(
          input = input,
          format = format,
          output = output,
        )

      result shouldBe Right(())
      runner.invocationCount shouldBe 1
      runner.recordedInput shouldBe Some(input)
      runner.recordedOutput shouldBe Some(output)
      runner.recordedTarget.map(_.label) shouldBe Some(format)

  it should "propagate the script runner failure when rendering fails" in {
    val runner = new RecordingScriptRunner(
      Left(ApplicationError.ScriptExecutionFailed("boom")),
    )
    val command =
      new RenderCommand(
        registry = DefaultRendererRegistry.live,
        runner = runner,
      )

    val format = DefaultRendererRegistry.live.supportedLabels.head

    val result =
      command.run(
        input = os.Path("deck.sc", os.pwd),
        format = format,
        output = os.Path("deck.out", os.pwd),
      )

    result shouldBe Left(ApplicationError.ScriptExecutionFailed("boom"))
  }

private final class RecordingScriptRunner(
  nextResult: Either[ApplicationError, Unit]) extends ScriptRunner:

  var invocationCount: Int = 0
  var recordedInput: Option[os.Path] = None
  var recordedTarget: Option[RenderFormat] = None
  var recordedOutput: Option[os.Path] = None

  override def render(
    input: os.Path,
    target: RenderFormat,
    output: os.Path,
  ): Either[ApplicationError, Unit] =
    invocationCount += 1
    recordedInput = Some(input)
    recordedTarget = Some(target)
    recordedOutput = Some(output)
    nextResult
