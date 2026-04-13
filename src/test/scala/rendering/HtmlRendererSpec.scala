package rendering

import declslides.domain._
import declslides.dsl.DSL._
import declslides.rendering.RenderingTarget.Html
import declslides.rendering.html.HtmlRenderer
import org.scalatest.flatspec.AnyFlatSpec

class HtmlRendererSpec extends AnyFlatSpec with RendererSpecSupport:

  behavior of "HtmlRenderer"

  override protected val renderer = HtmlRenderer()

  it should "use the html rendering target" in:
    val document = render(
      slide("Intro"):
        text("Hello"),
    )

    document.target shouldBe Html

  it should "use html as file extension" in:
    val document = render(
      slide("Intro"):
        text("Hello"),
    )

    document.fileExtension shouldBe "html"

  it should "render a valid html skeleton" in:
    val html = singleSlideContent():
      text("Hello")

    html should (
      include("<!DOCTYPE html>") and
        include("<html") and
        include("</html>")
    )

  it should "render the presentation title inside the title tag" in:
    val html = singleSlideContent():
      text("Hello")

    html should include("<title>Demo</title>")

  it should "render a section for each slide" in:
    val html = renderedContent(
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
    val html = singleSlideContentWithLayout("Focus", Layout.Centered):
      text("Important")

    html should include("""class="slide centered"""")

  it should "render paragraphs using p tags" in:
    val html = singleSlideContent():
      text("Hello")

    html should include("<p>Hello</p>")

  it should "render bullet lists using ul and li tags" in:
    val html = singleSlideContent("Bullets"):
      bullets("One", "Two")

    html should (
      include("<ul>") and
        include("<li>One</li>") and
        include("<li>Two</li>")
    )

  it should "render code blocks using pre and code tags" in:
    val html = singleSlideContent("Code"):
      code("scala", "val x = 42")

    html should (
      include("<pre><code") and
        include("language-scala") and
        include("val x = 42")
    )

  it should "escape html-sensitive code content" in:
    val html = renderedContent(
      slide("Code"):
        code("scala", """println("<tag>")"""),
    )

    html should include("&lt;tag&gt;")
    html should not include "<tag>"

  it should "render paragraphs and bullet lists in the same slide" in:
    val html = singleSlideContent("Mixed")(
      text("Hello"),
      bullets("One", "Two"),
    )

    html should (
      include("<p>Hello</p>") and
        include("<li>One</li>") and
        include("<li>Two</li>")
    )

  it should "include theme metadata" in:
    val html = renderedContent(
      theme(Theme.conference),
      slide("Intro"):
        text("Hello"),
    )

    html should include("""data-theme="conference"""")
