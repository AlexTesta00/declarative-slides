package declslides.application

import declslides.rendering.RenderFormat

trait ScriptRunner:

  def render(
    input: os.Path,
    target: RenderFormat,
    output: os.Path,
  ): Either[ApplicationError, Unit]
