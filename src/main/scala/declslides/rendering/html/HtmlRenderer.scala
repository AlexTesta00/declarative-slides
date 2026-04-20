package declslides.rendering.html

import declslides.domain.Layout
import declslides.domain.Presentation
import declslides.domain.Slide
import declslides.domain.SlideElement
import declslides.rendering.Document
import declslides.rendering.RenderFormat
import declslides.rendering.Renderer
import scalatags.Text.all._
import scalatags.Text.tags2.section

object HtmlRenderer extends Renderer:

  val Target: RenderFormat =
    RenderFormat(
      label = "html",
      fileExtension = "html",
      acceptedInputs = Set("html"),
    )

  private val styleTag = tag("style")
  private val titleTag = tag("title")
  private val mainTag = tag("main")

  override val target: RenderFormat =
    Target

  override def render(presentation: Presentation): Document =
    val page =
      doctype("html")(
        html(lang := "en")(
          renderHead(presentation),
          renderBody(presentation),
        ),
      )

    Document(
      target = target,
      content = page.render,
    )

  private def renderHead(presentation: Presentation): Frag =
    head(
      meta(charset := "UTF-8"),
      meta(
        name := "viewport",
        content := "width=device-width, initial-scale=1.0",
      ),
      titleTag(presentation.title),
      styleTag(HtmlRendererStyles.render(presentation.theme)),
    )

  private def renderBody(presentation: Presentation): Frag =
    body(
      mainTag(
        cls := "presentation",
        attr("data-theme") := presentation.theme.name,
      )(
        header(cls := "deck-header")(
          h1(presentation.title),
        ),
        presentation.slides.zipWithIndex.map { case (slide, index) =>
          renderSlide(index + 1, slide)
        },
      ),
    )

  private def renderSlide(
    number: Int,
    slide: Slide,
  ): Frag =
    section(
      cls := s"slide ${layoutClass(slide.layout)}",
      attr("data-slide") := number.toString,
    )(
      h2(slide.title),
      slide.elements.map(renderElement),
    )

  private def renderElement(element: SlideElement): Frag =
    element match
      case SlideElement.Paragraph(value) =>
        p(value)

      case SlideElement.BulletList(items) =>
        ul(items.map(li(_)))

      case SlideElement.CodeBlock(language, source) =>
        pre(
          code(cls := s"language-$language")(source),
        )

      case SlideElement.Spacer(lines) =>
        div(style := s"height: ${lines}rem;")

  private def layoutClass(layout: Layout): String =
    layout match
      case Layout.Flow =>
        "flow"

      case Layout.Centered =>
        "centered"
