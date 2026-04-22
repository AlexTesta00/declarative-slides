package declslides.application

/** Rendering request expressed in application-layer terms.
  *
  * Grouping input, output, and format into one value makes the render use case
  * easier to pass around and evolve over time.
  *
  * @param input
  *   input script to render
  * @param requestedFormat
  *   raw format requested by the caller
  * @param output
  *   destination file for the rendered document
  */
final case class RenderRequest(
  input: os.Path,
  requestedFormat: String,
  output: os.Path)
