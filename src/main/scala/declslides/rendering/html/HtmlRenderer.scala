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

/** HTML renderer for validated presentations.
  *
  * This renderer produces a self-contained HTML document with Tailwind loaded
  * from a CDN and a slide-oriented layout intended for full-screen navigation.
  */
object HtmlRenderer extends Renderer:

  /** HTML rendering target metadata. */
  val Target: RenderFormat =
    RenderFormat(
      label = "html",
      fileExtension = "html",
      acceptedInputs = Set("html"),
    )

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
      script(src := "https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"),
      renderNavigationScript,
    )

  private def renderBody(presentation: Presentation): Frag =
    body(
      cls := bodyClasses(presentation),
    )(
      renderDeckBadge(presentation),
      mainTag(
        id := "presentation-root",
        cls := "h-screen overflow-y-auto snap-y snap-mandatory scroll-smooth",
        attr("data-theme") := presentation.theme.name,
      )(
        presentation.slides.zipWithIndex.map { case (slide, index) =>
          renderSlide(
            number = index + 1,
            slide = slide,
            presentation = presentation,
          )
        },
      ),
    )

  private def renderDeckBadge(presentation: Presentation): Frag =
    div(
      cls :=
        s"pointer-events-none fixed left-4 top-4 z-10 rounded-full px-4 py-2 text-sm font-medium shadow-lg backdrop-blur bg-[${presentation.theme.codeBackground}] text-[${presentation.theme.accent}]",
    )(
      presentation.title,
    )

  private def renderSlide(
    number: Int,
    slide: Slide,
    presentation: Presentation,
  ): Frag =
    section(
      cls := slideClasses(slide),
      attr("data-slide") := number.toString,
    )(
      div(
        cls := "mx-auto flex w-full max-w-6xl flex-col gap-8",
      )(
        div(
          cls := "flex items-center justify-between gap-4",
        )(
          h2(
            cls :=
              s"text-4xl font-bold tracking-tight md:text-6xl text-[${presentation.theme.accent}]",
          )(
            slide.title,
          ),
          span(
            cls :=
              s"shrink-0 rounded-full px-3 py-1 text-xs font-semibold uppercase tracking-[0.2em] bg-[${presentation.theme.codeBackground}] text-[${presentation.theme.foreground}]",
          )(
            f"$number%02d",
          ),
        ),
        div(
          cls := contentContainerClasses(slide.layout),
        )(
          slide.elements.map(element => renderElement(element, presentation)),
        ),
      ),
    )

  private def renderElement(
    element: SlideElement,
    presentation: Presentation,
  ): Frag =
    element match
      case SlideElement.Paragraph(value) =>
        p(
          cls := "max-w-4xl text-lg leading-8 md:text-2xl",
        )(
          value,
        )

      case SlideElement.BulletList(items) =>
        ul(
          cls :=
            "max-w-4xl list-disc space-y-3 pl-6 text-lg leading-8 md:text-2xl",
        )(
          items.map(item => li(item)),
        )

      case SlideElement.CodeBlock(language, source) =>
        pre(
          cls :=
            s"w-full max-w-5xl overflow-x-auto rounded-3xl p-6 text-sm leading-7 shadow-2xl md:p-8 md:text-base bg-[${presentation.theme.codeBackground}] text-[${presentation.theme.foreground}]",
        )(
          code(
            cls := s"language-$language block font-mono",
          )(
            source,
          ),
        )

      case SlideElement.Spacer(lines) =>
        div(
          cls := s"w-full shrink-0",
          style := s"height: ${lines}rem;",
        )

  private def bodyClasses(presentation: Presentation): String =
    s"m-0 h-screen overflow-hidden font-sans bg-[${presentation.theme.background}] text-[${presentation.theme.foreground}]"

  private def slideClasses(slide: Slide): String =
    s"h-screen min-h-screen w-full snap-start px-8 py-10 md:px-16 md:py-14 lg:px-24 ${
        layoutClasses(slide.layout)
      }"

  private def contentContainerClasses(layout: Layout): String =
    layout match
      case Layout.Flow =>
        "flex w-full flex-col gap-6 items-start text-left"

      case Layout.Centered =>
        "flex w-full flex-1 flex-col items-center justify-center gap-6 text-center"

  private def layoutClasses(layout: Layout): String =
    layout match
      case Layout.Flow =>
        "flex items-center"

      case Layout.Centered =>
        "flex items-center"

  private def renderNavigationScript: Frag =
    HtmlNavigationScript.content match
      case Right(scriptContent) =>
        script(
          raw(scriptContent),
        )

      case Left(error) =>
        script(
          raw(
            s"""console.error(${toJsStringLiteral(error)});""",
          ),
        )

  private def toJsStringLiteral(value: String): String =
    val escaped =
      value
        .flatMap {
          case '\\' => "\\\\"
          case '"' => "\\\""
          case '\n' => "\\n"
          case '\r' => "\\r"
          case '\t' => "\\t"
          case c => c.toString
        }

    s""""$escaped""""
