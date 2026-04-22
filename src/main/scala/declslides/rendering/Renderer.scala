package declslides.rendering

import declslides.domain.Presentation

type Body = String
type FileExtension = String

/** Rendering target metadata understood by the rendering layer.
  *
  * A `RenderFormat` describes how a renderer is addressed by users and what
  * file extension it naturally produces.
  *
  * @param label
  *   canonical label for the format
  * @param fileExtension
  *   default file extension produced by the renderer
  * @param acceptedInputs
  *   raw labels accepted when parsing a user request
  */
final case class RenderFormat(
  label: String,
  fileExtension: FileExtension,
  acceptedInputs: Set[String]):

  /** Returns `true` when the raw label is accepted by this format. */
  def accepts(raw: String): Boolean =
    acceptedInputs.contains(raw.toLowerCase)

/** Rendered document produced by a renderer.
  *
  * @param target
  *   format used to render the document
  * @param content
  *   serialized document body
  */
final case class Document(
  target: RenderFormat,
  content: Body):

  /** Returns the file extension associated with the document target. */
  def fileExtension: FileExtension =
    target.fileExtension

/** Component that turns a validated [[declslides.domain.Presentation]] into a
  * [[Document]].
  */
trait Renderer:
  /** Target format supported by this renderer. */
  def target: RenderFormat

  /** Renders the given presentation. */
  def render(presentation: Presentation): Document
