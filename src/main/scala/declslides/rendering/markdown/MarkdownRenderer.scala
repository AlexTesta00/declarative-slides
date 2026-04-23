package declslides.rendering.markdown

import declslides.domain.Layout
import declslides.domain.Presentation
import declslides.domain.Slide
import declslides.domain.SlideElement
import declslides.rendering.Document
import declslides.rendering.RenderFormat
import declslides.rendering.Renderer

/** Markdown renderer for validated presentations.
  *
  * This renderer produces a Markdown document with a simple structure and
  * minimal formatting, suitable for further processing or conversion.
  */
object MarkdownRenderer extends Renderer:

  /** Markdown rendering target metadata. */
  val Target: RenderFormat =
    RenderFormat(
      label = "markdown",
      fileExtension = "md",
      acceptedInputs = Set("markdown", "md"),
    )

  override val target: RenderFormat =
    Target

  override def render(presentation: Presentation): Document =
    Document(
      target = target,
      content = renderContent(presentation),
    )

  private def renderContent(presentation: Presentation): String =
    Seq(
      s"# ${presentation.title}",
      s"_Theme: ${presentation.theme.name}_",
      renderSlides(presentation),
    ).mkString("\n\n")

  private def renderSlides(presentation: Presentation): String =
    presentation.slides
      .map(renderSlide)
      .mkString("\n\n")

  private def renderSlide(slide: Slide): String =
    val blocks =
      Vector(s"## ${slide.title}") ++
        layoutMarker(slide.layout).toVector ++
        slide.elements.map(renderElementBlock)

    blocks.mkString("\n\n")

  private def renderElementBlock(element: SlideElement): String =
    element match
      case SlideElement.Paragraph(value) =>
        value

      case SlideElement.BulletList(items) =>
        items.map(item => s"- $item").mkString("\n")

      case SlideElement.CodeBlock(language, source) =>
        Seq(
          s"```$language",
          source,
          "```",
        ).mkString("\n")

      case SlideElement.Spacer(lines) =>
        List.fill(lines)("").mkString("\n")

      case SlideElement.Image(source, altText) =>
        renderImageBlock(source, altText)

  private def renderImageBlock(
    source: String,
    altText: String,
  ): String =
    s"![$altText]($source)"

  private def layoutMarker(
    layout: Layout,
  ): Option[String] =
    layout match
      case Layout.Flow =>
        None

      case Layout.Centered =>
        Some("<!-- layout: centered -->")
