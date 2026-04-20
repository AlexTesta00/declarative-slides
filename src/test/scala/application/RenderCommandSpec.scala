package application

import declslides.application.ApplicationError
import declslides.application.RenderCommand
import declslides.application.ScriptRunner
import declslides.domain.Presentation
import declslides.rendering.Document
import declslides.rendering.RenderFormat
import declslides.rendering.Renderer
import declslides.rendering.RendererRegistry
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RenderCommandSpec extends AnyFlatSpec with Matchers:

  behavior of "RenderCommand"

  private val htmlTarget =
    RenderFormat(
      label = "html",
      fileExtension = "html",
      acceptedInputs = Set("html"),
    )

  private object HtmlRendererStub extends Renderer:

    override val target: RenderFormat =
      htmlTarget

    override def render(presentation: Presentation): Document =
      Document(
        target = target,
        content = "<html></html>",
      )

  private final class RecordingRunner(
    result: Either[ApplicationError, Unit]) extends ScriptRunner:

    var recordedInput: Option[os.Path] = None
    var recordedTarget: Option[RenderFormat] = None
    var recordedOutput: Option[os.Path] = None
    var invocationCount: Int = 0

    override def render(
      input: os.Path,
      target: RenderFormat,
      output: os.Path,
    ): Either[ApplicationError, Unit] =
      invocationCount = invocationCount + 1
      recordedInput = Some(input)
      recordedTarget = Some(target)
      recordedOutput = Some(output)
      result

  private val registry =
    RendererRegistry(HtmlRendererStub)

  it should "delegate to the runner when the format is supported" in:
    val runner =
      RecordingRunner(Right(()))

    val command =
      RenderCommand(
        registry = registry,
        runner = runner,
      )

    val input =
      os.Path("examples/HelloPresentation.sc", os.pwd)

    val output =
      os.Path("out/HelloPresentation.html", os.pwd)

    val result =
      command.run(
        input = input,
        format = "html",
        output = output,
      )

    result shouldBe Right(())
    runner.invocationCount shouldBe 1
    runner.recordedInput shouldBe Some(input)
    runner.recordedTarget shouldBe Some(htmlTarget)
    runner.recordedOutput shouldBe Some(output)

  it should "return an unsupported format error when the format is unknown" in:
    val runner =
      RecordingRunner(Right(()))

    val command =
      RenderCommand(
        registry = registry,
        runner = runner,
      )

    val result =
      command.run(
        input = os.Path("examples/HelloPresentation.sc", os.pwd),
        format = "pdf",
        output = os.Path("out/HelloPresentation.pdf", os.pwd),
      )

    result shouldBe Left(
      ApplicationError.UnsupportedFormat(
        raw = "pdf",
        supported = Vector("html"),
      ),
    )

    runner.invocationCount shouldBe 0

  it should "propagate runner failures" in:
    val expectedError =
      ApplicationError.ScriptExecutionFailed("subprocess failed")

    val runner =
      RecordingRunner(Left(expectedError))

    val command =
      RenderCommand(
        registry = registry,
        runner = runner,
      )

    val result =
      command.run(
        input = os.Path("examples/HelloPresentation.sc", os.pwd),
        format = "html",
        output = os.Path("out/HelloPresentation.html", os.pwd),
      )

    result shouldBe Left(expectedError)
    runner.invocationCount shouldBe 1
