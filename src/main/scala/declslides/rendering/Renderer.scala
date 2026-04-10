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
  override val target: RenderingTarget = RenderingTarget.Html

  override def render(presentation: Presentation): Document =
    val theme = presentation.theme
    val slidesHtml =
      presentation.slides.zipWithIndex
        .map { case (s, i) => renderSlide(i + 1, s) }
        .mkString("\n")

    val content =
      s"""<!DOCTYPE html>
         |<html lang="en">
         |<head>
         |  <meta charset="UTF-8" />
         |  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
         |  <title>${escape(presentation.title)}</title>
         |  <style>
         |    body {
         |      margin: 0;
         |      font-family: system-ui, sans-serif;
         |      background: ${theme.background};
         |      color: ${theme.foreground};
         |    }
         |    .presentation {
         |      max-width: 1100px;
         |      margin: 0 auto;
         |      padding: 2rem;
         |    }
         |    .deck-header {
         |      border-bottom: 2px solid ${theme.accent};
         |      margin-bottom: 2rem;
         |      padding-bottom: 1rem;
         |    }
         |    .slide {
         |      min-height: 70vh;
         |      padding: 2rem 0;
         |      border-bottom: 1px solid rgba(255,255,255,0.15);
         |      display: flex;
         |      flex-direction: column;
         |      gap: 1rem;
         |    }
         |    .slide.centered {
         |      justify-content: center;
         |      align-items: center;
         |      text-align: center;
         |    }
         |    .slide h2 {
         |      color: ${theme.accent};
         |      margin-bottom: 1rem;
         |    }
         |    pre {
         |      background: ${theme.codeBackground};
         |      padding: 1rem;
         |      border-radius: 8px;
         |      overflow-x: auto;
         |    }
         |    code {
         |      font-family: ui-monospace, monospace;
         |    }
         |    ul {
         |      line-height: 1.7;
         |    }
         |  </style>
         |</head>
         |<body>
         |  <main class="presentation" data-theme="${escape(theme.name)}">
         |    <header class="deck-header">
         |      <h1>${escape(presentation.title)}</h1>
         |    </header>
         |    $slidesHtml
         |  </main>
         |</body>
         |</html>
         |""".stripMargin

    Document(target, content, "html")

  private def renderSlide(
    number: Int,
    slide: Slide,
  ): String =
    val layoutClass = slide.layout match
      case Layout.Flow => "flow"
      case Layout.Centered => "centered"

    val elementsHtml =
      slide.elements.map(renderElement).mkString("\n")

    s"""<section class="slide $layoutClass" data-slide="$number">
       |  <h2>${escape(slide.title)}</h2>
       |  $elementsHtml
       |</section>""".stripMargin

  private def renderElement(element: SlideElement): String = element match
    case SlideElement.Paragraph(value) =>
      s"<p>${escape(value)}</p>"

    case SlideElement.BulletList(items) =>
      val renderedItems =
        items.map(item => s"<li>${escape(item)}</li>").mkString("\n")
      s"<ul>\n$renderedItems\n</ul>"

    case SlideElement.CodeBlock(language, source) =>
      s"""<pre><code class="language-${escape(language)}">${escape(
          source,
        )}</code></pre>"""

    case SlideElement.Spacer(lines) =>
      s"""<div style="height: ${lines}rem;"></div>"""

  private def escape(value: String): String =
    value
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;")
