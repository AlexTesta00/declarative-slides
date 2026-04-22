package declslides.application

import declslides.rendering.RenderFormat

/** Executes the concrete rendering of a presentation script.
  *
  * A `ScriptRunner` hides the details of how the script is evaluated. The
  * current production implementation uses Scala CLI, but the rest of the
  * application does not need to know that.
  */
trait ScriptRunner:

  /** Renders the input script into the requested output target.
    *
    * @param input
    *   source script to execute
    * @param target
    *   resolved rendering target
    * @param output
    *   destination file
    */
  def render(
    input: os.Path,
    target: RenderFormat,
    output: os.Path,
  ): Either[ApplicationError, Unit]
