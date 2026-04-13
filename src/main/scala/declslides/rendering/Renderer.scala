package declslides.rendering

import declslides.domain.Layout
import declslides.domain.Presentation
import declslides.domain.Slide
import declslides.domain.SlideElement
import declslides.rendering.RenderingTarget.Text

type Body = String
type FileExtension = String

enum RenderingTarget derives CanEqual:
  case Text
  case Html

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

final class HtmlRenderer extends Renderer:

  import scalatags.Text.all.*
  import scalatags.Text.tags2.section

  private val styleTag = tag("style")
  private val titleTag = tag("title")
  private val mainTag = tag("main")

  override val target: RenderingTarget = RenderingTarget.Html

  override def render(presentation: Presentation): Document =
    val theme = presentation.theme

    val page =
      doctype("html")(
        html(lang := "en")(
          head(
            meta(charset := "UTF-8"),
            meta(
              name := "viewport",
              content := "width=device-width, initial-scale=1.0",
            ),
            titleTag(presentation.title),
            styleTag(
              s"""
            body {
              margin: 0;
              font-family: system-ui, sans-serif;
              background: ${theme.background};
              color: ${theme.foreground};
            }
            .presentation {
              max-width: 1100px;
              margin: 0 auto;
              padding: 2rem;
            }
            .deck-header {
              border-bottom: 2px solid ${theme.accent};
              margin-bottom: 2rem;
              padding-bottom: 1rem;
            }
            .slide {
              min-height: 70vh;
              padding: 2rem 0;
              border-bottom: 1px solid rgba(255,255,255,0.15);
              display: flex;
              flex-direction: column;
              gap: 1rem;
            }
            .slide.centered {
              justify-content: center;
              align-items: center;
              text-align: center;
            }
            .slide h2 {
              color: ${theme.accent};
              margin-bottom: 1rem;
            }
            pre {
              background: ${theme.codeBackground};
              padding: 1rem;
              border-radius: 8px;
              overflow-x: auto;
            }
            code {
              font-family: ui-monospace, monospace;
            }
            ul {
              line-height: 1.7;
            }
            """,
            ),
          ),
          body(
            mainTag(
              cls := "presentation",
              attr("data-theme") := theme.name,
            )(
              header(cls := "deck-header")(
                h1(presentation.title),
              ),
              for (slide, index) <- presentation.slides.zipWithIndex
              yield renderSlide(index + 1, slide),
            ),
          ),
        ),
      )

    Document(target, page.render, "html")

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
        ul(
          items.map(item => li(item)),
        )

      case SlideElement.CodeBlock(language, source) =>
        pre(
          code(cls := s"language-$language")(source),
        )

      case SlideElement.Spacer(lines) =>
        div(height := s"${lines}rem")

  private def layoutClass(layout: Layout): String =
    layout match
      case Layout.Flow => "flow"
      case Layout.Centered => "centered"
