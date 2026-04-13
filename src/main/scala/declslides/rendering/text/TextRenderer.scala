package declslides.rendering.text

import declslides.domain.Presentation
import declslides.domain.Slide
import declslides.domain.SlideElement
import declslides.rendering.Body
import declslides.rendering.Document
import declslides.rendering.Renderer
import declslides.rendering.RenderingTarget

final class TextRenderer extends Renderer:

  override val target: RenderingTarget = RenderingTarget.Text

  override def render(presentation: Presentation): Document =
    Document(
      target = target,
      content = renderContent(presentation),
      fileExtension = "txt",
    )

  private def renderContent(presentation: Presentation): Body =
    Seq(
      TextRendererFormatting.renderTitle(presentation),
      TextRendererFormatting.renderTheme(presentation),
      "",
      renderSlides(presentation),
    ).mkString("\n")

  private def renderSlides(presentation: Presentation): String =
    presentation.slides.zipWithIndex
      .map { case (slide, index) => renderSlide(slide, index + 1) }
      .mkString("\n\n")

  private def renderSlide(
    slide: Slide,
    number: Int,
  ): String =
    val lines =
      TextRendererFormatting.renderSlideHeader(slide, number) +:
        slide.elements.flatMap(renderElementLines)

    lines.mkString("\n")

  private def renderElementLines(element: SlideElement): Seq[String] =
    element match
      case SlideElement.Paragraph(value) =>
        TextRendererFormatting.renderParagraph(value)

      case SlideElement.BulletList(items) =>
        TextRendererFormatting.renderBulletList(items)

      case SlideElement.CodeBlock(language, source) =>
        TextRendererFormatting.renderCodeBlock(language, source)

      case SlideElement.Spacer(lines) =>
        TextRendererFormatting.renderSpacer(lines)
