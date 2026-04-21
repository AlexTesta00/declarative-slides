package declslides.application

final case class RenderRequest(
  input: os.Path,
  requestedFormat: String,
  output: os.Path)
