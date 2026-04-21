package declslides.application

import declslides.rendering.RendererRegistry

final class RenderCommand private (
  formatResolver: RenderFormatResolver,
  runner: ScriptRunner):

  def this(
    registry: RendererRegistry,
    runner: ScriptRunner,
  ) =
    this(
      formatResolver = new RegistryRenderFormatResolver(registry),
      runner = runner,
    )

  def run(
    input: os.Path,
    format: String,
    output: os.Path,
  ): Either[ApplicationError, Unit] =
    run(
      RenderRequest(
        input = input,
        requestedFormat = format,
        output = output,
      ),
    )

  def run(
    request: RenderRequest,
  ): Either[ApplicationError, Unit] =
    for
      target <- formatResolver.resolve(request.requestedFormat)
      _ <- runner.render(
        input = request.input,
        target = target,
        output = request.output,
      )
    yield ()
