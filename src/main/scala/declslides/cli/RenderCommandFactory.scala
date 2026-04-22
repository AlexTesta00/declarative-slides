package declslides.cli

import declslides.application.RenderCommand
import declslides.application.ScalaCliScriptRunner
import declslides.rendering.DefaultRendererRegistry

/** Creates fully wired rendering commands for the CLI layer.
  *
  * This abstraction keeps infrastructure wiring out of the command-line
  * orchestration, which makes the CLI easier to read and easier to test.
  */
trait RenderCommandFactory:
  /** Creates a rendering command or returns a CLI-facing initialization error.
    */
  def create(): Either[CliError, RenderCommand]

/** Default production factory for `RenderCommand` instances.
  *
  * This object bridges the CLI layer with the concrete renderer registry and
  * the Scala CLI-based script runner used at runtime.
  */
object DefaultRenderCommandFactory extends RenderCommandFactory:

  override def create(): Either[CliError, RenderCommand] =
    DeclSlidesRuntime.scalaCliBinary
      .left
      .map(CliError.RuntimeInitialization.apply)
      .map { scalaCliBinary =>
        RenderCommand(
          registry = DefaultRendererRegistry.live,
          runner = ScalaCliScriptRunner(
            declslidesDependency = DeclSlidesRuntime.coreDependency,
            scalaCliBinary = scalaCliBinary,
            scalaVersion = DeclSlidesRuntime.scalaVersion,
          ),
        )
      }
