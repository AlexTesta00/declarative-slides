package declslides.rendering

import declslides.domain.Presentation
import declslides.domain.Slide
import declslides.domain.SlideElement
import declslides.rendering.RenderingTarget.Text

type Body = String
type FileExtension = String

enum RenderingTarget:
  case Text

final case class Document(
  target: RenderingTarget,
  content: Body,
  fileExtension: FileExtension)

trait Renderer:
  def target: RenderingTarget
  def render(presentation: Presentation): Document

final class TextRenderer extends Renderer:
  override def target: RenderingTarget = RenderingTarget.Text

  override def render(presentation: Presentation): Document =
    val slideText =
      presentation.slides.zipWithIndex.map { case (slide, i) =>
        renderSlide(slide, i + 1)
      }.mkString("\n\n")

    val content =
      Seq(
        presentation.title,
        s"Theme: ${presentation.theme.name}",
        "",
        slideText,
      ).mkString("\n")

    Document(Text, content, "txt")

  private def renderSlide(
    slide: Slide,
    number: Int,
  ): String =
    val lines =
      s"[$number] ${slide.title} (${slide.layout})" +:
        slide.elements.flatMap(renderElementLines)

    lines.mkString("\n")

  private def renderElementLines(element: SlideElement): Seq[String] =
    element match
      case SlideElement.Paragraph(value) => Seq(value)
      case SlideElement.BulletList(items) => items.map(item => s"- $item")
      case SlideElement.CodeBlock(language, code) =>
        Seq(s"```$language", code, "```")
      case SlideElement.Spacer(lines) => List.fill(lines)("")
