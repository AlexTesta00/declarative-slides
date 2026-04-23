package rendering

import declslides.domain.Layout
import declslides.dsl.DSL._
import declslides.rendering.markdown.MarkdownRenderer
import org.scalatest.flatspec.AnyFlatSpec

class MarkdownRendererSpec extends AnyFlatSpec with RendererSpecSupport:

  behavior of "MarkdownRenderer"

  override protected val renderer: MarkdownRenderer.type = MarkdownRenderer

  private val markdownFormat = MarkdownRenderer.Target

  it should "use the markdown rendering target" in:
    val document = render(
      slide("Intro"):
        text("Hello"),
    )

    document.target.shouldBe(markdownFormat)

  it should "use md as file extension" in:
    val document = render(
      slide("Intro"):
        text("Hello"),
    )

    document.fileExtension.shouldBe("md")

  it should "render the presentation title as a level one heading" in:
    val content = singleSlideContent():
      text("Hello")

    content.should(include("# Demo"))

  it should "render the theme name" in:
    val content = singleSlideContent():
      text("Hello")

    content.should(include("_Theme: default_"))

  it should "render slide titles as level two headings" in:
    val content = renderedContent(
      slide("One"):
        text("A")
      ,
      slide("Two"):
        text("B"),
    )

    content.should(
      include("## One") and
        include("## Two"),
    )

  it should "render bullet lists using markdown bullet syntax" in:
    val content = singleSlideContent("Bullets"):
      bullets("One", "Two")

    content.should(
      include("- One") and
        include("- Two"),
    )

  it should "render code blocks using fenced code syntax" in:
    val content = singleSlideContent("Code"):
      code("scala", "val x = 42")

    content.should(
      include("```scala") and
        include("val x = 42") and
        include("```"),
    )

  it should "render images using markdown image syntax" in:
    val content = singleSlideContent("Media"):
      image("./images/logo.png", "Company logo")

    content.should(include("![Company logo](./images/logo.png)"))

  it should "render centered slides with a layout marker" in:
    val content = singleSlideContentWithLayout("Focus", Layout.Centered):
      text("Important")

    content.should(include("<!-- layout: centered -->"))
