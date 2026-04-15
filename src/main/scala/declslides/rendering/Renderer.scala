package declslides.rendering

import declslides.domain.Presentation

type Body = String
type FileExtension = String

final case class RenderFormat(
  label: String,
  fileExtension: FileExtension,
  acceptedInputs: Set[String]):

  def accepts(raw: String): Boolean =
    acceptedInputs.contains(raw.toLowerCase)

final case class Document(
  target: RenderFormat,
  content: Body):

  def fileExtension: FileExtension =
    target.fileExtension

trait Renderer:
  def target: RenderFormat
  def render(presentation: Presentation): Document
