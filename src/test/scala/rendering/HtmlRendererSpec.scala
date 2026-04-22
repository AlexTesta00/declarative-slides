package rendering

import declslides.domain._
import declslides.dsl.DSL._
import declslides.rendering.html.HtmlRenderer
import org.scalatest.flatspec.AnyFlatSpec

class HtmlRendererSpec extends AnyFlatSpec with RendererSpecSupport:

  behavior of "HtmlRenderer"

  override protected val renderer: HtmlRenderer.type = HtmlRenderer

  private val htmlFormat = HtmlRenderer.Target

  private def expectRight(
    result: Either[Vector[DomainError], Presentation],
  ): Presentation =
    result match
      case Right(presentation) =>
        presentation
      case Left(errors) =>
        fail(s"Expected Right(Presentation), got Left($errors)")

  private def themedContent(
    theme: Theme,
    slides: PresBuild*,
  ): String =
    renderer.render(
      expectRight(
        presentation("Demo").use(theme) {
          deck(slides*)
        },
      ),
    ).content

  it should "use the html rendering target" in:
    val document = render(
      slide("Intro"):
        text("Hello"),
    )

    document.target.shouldBe(htmlFormat)

  it should "use html as file extension" in:
    val document = render(
      slide("Intro"):
        text("Hello"),
    )

    document.fileExtension.shouldBe("html")

  it should "render a valid html skeleton" in:
    val html = singleSlideContent():
      text("Hello")

    html.should(
      include("<!DOCTYPE html>") and
        include("<html") and
        include("</html>"),
    )

  it should "include the tailwind browser cdn script" in:
    val html = singleSlideContent():
      text("Hello")

    html.should(
      include("""https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"""),
    )

  it should "render the presentation title inside the title tag" in:
    val html = singleSlideContent():
      text("Hello")

    html.should(include("<title>Demo</title>"))

  it should "render a full screen section for each slide" in:
    val html = renderedContent(
      slide("One"):
        text("A")
      ,
      slide("Two"):
        text("B"),
    )

    html.should(
      include("""data-slide="1"""") and
        include("""data-slide="2"""") and
        include("h-screen") and
        include("snap-start"),
    )

  it should "render centered slides with centered content classes" in:
    val html = singleSlideContentWithLayout("Focus", Layout.Centered):
      text("Important")

    html.should(
      include("items-center") and
        include("justify-center") and
        include("text-center"),
    )

  it should "render paragraphs using p tags" in:
    val html = singleSlideContent():
      text("Hello")

    html.should(include("<p"))
    html.should(include(">Hello</p>"))

  it should "render bullet lists using ul and li tags" in:
    val html = singleSlideContent("Bullets"):
      bullets("One", "Two")

    html.should(
      include("<ul") and
        include("<li>One</li>") and
        include("<li>Two</li>"),
    )

  it should "render code blocks using pre and code tags" in:
    val html = singleSlideContent("Code"):
      code("scala", "val x = 42")

    html.should(
      include("<pre") and
        include("<code") and
        include("language-scala") and
        include("val x = 42"),
    )

  it should "escape html-sensitive code content" in:
    val html = renderedContent(
      slide("Code"):
        code("scala", """println("<tag>")"""),
    )

    html.should(include("&lt;tag&gt;"))
    html.shouldNot(include("<tag>"))

  it should "render paragraphs and bullet lists in the same slide" in:
    val html = singleSlideContent("Mixed")(
      text("Hello"),
      bullets("One", "Two"),
    )

    html.should(
      include("<p") and
        include(">Hello</p>") and
        include("<li>One</li>") and
        include("<li>Two</li>"),
    )

  it should "include theme metadata" in:
    val html =
      themedContent(
        Theme.conference,
        slide("Intro"):
          text("Hello"),
      )

    html.should(include("""data-theme="conference""""))

  it should "include keyboard navigation for left and right arrows" in:
    val html = singleSlideContent():
      text("Hello")

    html.should(
      include("ArrowRight") and
        include("ArrowLeft") and
        include("scrollIntoView") and
        include("presentation-root"),
    )

  it should "embed a non empty navigation script" in:
    val html = singleSlideContent():
      text("Hello")

    html should include("<script>")
    html should not include "<script></script>"
