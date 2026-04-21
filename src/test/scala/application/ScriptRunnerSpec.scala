package application

import declslides.application.ApplicationError
import declslides.application.ScriptRunner
import declslides.rendering.DefaultRendererRegistry
import declslides.rendering.RenderFormat
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScriptRunnerSpec extends AnyFlatSpec with Matchers:

  behavior of "ScriptRunner"

  it should "allow implementations to return a successful rendering result" in:
    val runner = new FixedScriptRunner(Right(()))
    val target = supportedTarget()

    val result =
      runner.render(
        input = os.Path("deck.sc", os.pwd),
        target = target,
        output = os.Path("deck.html", os.pwd),
      )

    result shouldBe Right(())

  it should "allow implementations to return an application failure" in:
    val runner =
      new FixedScriptRunner(
        Left(ApplicationError.ScriptExecutionFailed("failed")),
      )
    val target = supportedTarget()

    val result =
      runner.render(
        input = os.Path("deck.sc", os.pwd),
        target = target,
        output = os.Path("deck.html", os.pwd),
      )

    result shouldBe Left(ApplicationError.ScriptExecutionFailed("failed"))

  private def supportedTarget(): RenderFormat =
    val registry = DefaultRendererRegistry.live
    registry
      .parse(registry.supportedLabels.head)
      .getOrElse(fail("Expected at least one supported render format"))

private final class FixedScriptRunner(
                                       result: Either[ApplicationError, Unit],
                                     ) extends ScriptRunner:

  override def render(
                       input: os.Path,
                       target: RenderFormat,
                       output: os.Path,
                     ): Either[ApplicationError, Unit] =
    result