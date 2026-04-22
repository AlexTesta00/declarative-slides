package declslides.application

import declslides.rendering.RendererRegistry

/** Application use case that renders a presentation script into an output file.
  *
  * The command resolves the requested format and delegates the actual execution
  * to a [[ScriptRunner]]. It does not deal with command-line concerns or with
  * low-level process wiring.
  *
  * @param formatResolver
  *   component that turns raw format labels into render targets
  * @param runner
  *   component that executes the actual rendering
  */
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

  /** Renders a presentation from raw input, format, and output values. */
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

  /** Renders a presentation from a single request value. */
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
