package rendering

import declslides.domain._
import declslides.dsl.DSL._
import declslides.rendering.HtmlRenderer
import declslides.rendering.RenderingTarget.Html
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HtmlRendererSpec extends AnyFlatSpec with Matchers:

  behavior of "HtmlRenderer"

  private val renderer = HtmlRenderer()

  private def demoDeck(items: => PresBuild*) =
    presentation("Demo"):
      deck(items*)
    .value

  private def render(items: PresBuild*) =
    renderer.render(demoDeck(items*))

  private def renderedHtml(items: PresBuild*): String =
    render(items*).content

  private def singleSlideHtml(title: String = "Intro")(items: SlideBuild*)
    : String =
    renderedHtml(
      slide(title):
        content(items*),
    )

  private def singleSlideHtmlWithLayout(
    title: String,
    layout: Layout,
  )(items: SlideBuild*,
  ): String =
    renderedHtml(
      slide(title, layout):
        content(items*),
    )

  it should "use the html rendering target" in:
    val document = render:
      slide("Intro"):
        text("Hello")

    document.target shouldBe Html

  it should "use html as file extension" in:
    val document = render:
      slide("Intro"):
        text("Hello")

    document.fileExtension shouldBe "html"

  it should "render a valid html skeleton" in:
    val html = singleSlideHtml():
      text("Hello")

    html should (
      include("<!DOCTYPE html>") and
        include("<html") and
        include("</html>")
    )

  it should "render the presentation title inside the title tag" in:
    val html = singleSlideHtml():
      text("Hello")

    html should include("<title>Demo</title>")

  it should "render a section for each slide" in:
    val html = renderedHtml(
      slide("One"):
        text("A")
      ,
      slide("Two"):
        text("B"),
    )

    html should (
      include("""data-slide="1"""") and
        include("""data-slide="2"""")
    )

  it should "render centered slides with a centered class" in:
    val html = singleSlideHtmlWithLayout("Focus", Layout.Centered):
      text("Important")

    html should include("""class="slide centered"""")

  it should "render paragraphs using p tags" in:
    val html = singleSlideHtml():
      text("Hello")

    html should include("<p>Hello</p>")

  it should "render bullet lists using ul and li tags" in:
    val html = singleSlideHtml("Bullets"):
      bullets("One", "Two")

    html should (
      include("<ul>") and
        include("<li>One</li>") and
        include("<li>Two</li>")
    )

  it should "render code blocks using pre and code tags" in:
    val html = singleSlideHtml("Code"):
      code("scala", "val x = 42")

    html should (
      include("<pre><code") and
        include("language-scala") and
        include("val x = 42")
    )

  it should "escape html-sensitive code content" in:
    val html = renderedHtml:
      slide("Code"):
        code("scala", """println("<tag>")""")

    html should include("&lt;tag&gt;")
    html should not include "<tag>"

  it should "render paragraphs and bullet lists in the same slide" in:
    val html = singleSlideHtml("Mixed")(
      text("Hello"),
      bullets("One", "Two"),
    )

    html should (
      include("<p>Hello</p>") and
        include("<li>One</li>") and
        include("<li>Two</li>")
    )

  it should "include theme metadata" in:
    val html = renderedHtml(
      theme(Theme.conference),
      slide("Intro"):
        text("Hello"),
    )

    html should include("""data-theme="conference"""")
