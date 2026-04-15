package declslides.rendering

import declslides.domain.Presentation

type Body = String
type FileExtension = String

final case class Document(
  target: RenderingTarget,
  content: Body,
  fileExtension: FileExtension)

trait Renderer:
  def target: RenderingTarget
  def render(presentation: Presentation): Document
