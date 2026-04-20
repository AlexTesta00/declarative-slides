package declslides.application

import declslides.rendering.RendererRegistry

final class RenderCommand(
  registry: RendererRegistry,
  runner: ScriptRunner):

  def run(
    input: os.Path,
    format: String,
    output: os.Path,
  ): Either[ApplicationError, Unit] =
    registry
      .parse(format)
      .toRight(
        ApplicationError.UnsupportedFormat(
          raw = format,
          supported = registry.supportedLabels,
        ),
      )
      .flatMap { target =>
        runner.render(
          input = input,
          target = target,
          output = output,
        )
      }
