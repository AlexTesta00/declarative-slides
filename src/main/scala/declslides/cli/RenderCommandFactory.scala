package declslides.cli

import declslides.application.RenderCommand
import declslides.application.ScalaCliScriptRunner
import declslides.rendering.DefaultRendererRegistry

trait RenderCommandFactory:
  def create(): Either[CliError, RenderCommand]

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
