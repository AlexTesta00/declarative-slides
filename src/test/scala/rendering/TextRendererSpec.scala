package rendering

import declslides.domain.Layout
import declslides.dsl.DSL._
import declslides.rendering.text.TextRenderer
import org.scalatest.flatspec.AnyFlatSpec

class TextRendererSpec extends AnyFlatSpec with RendererSpecSupport:

  behavior of "TextRenderer"

  override protected val renderer = TextRenderer

  private val textFormat = TextRenderer.Target

  it should "use the text rendering target" in:
    val document = render(
      slide("Intro"):
        text("Hello"),
    )

    document.target.shouldBe(textFormat)

  it should "use txt as file extension" in:
    val document = render(
      slide("Intro"):
        text("Hello"),
    )

    document.fileExtension.shouldBe("txt")

  it should "render the presentation title" in:
    val content = singleSlideContent():
      text("Hello")

    content.should(include("Demo"))

  it should "render the theme name" in:
    val content = singleSlideContent():
      text("Hello")

    content.should(include("Theme: default"))

  it should "preserve slide order" in:
    val content = renderedContent(
      slide("First"):
        text("A")
      ,
      slide("Second"):
        text("B"),
    )

    content.indexOf("First").should(be < content.indexOf("Second"))

  it should "number slides" in:
    val content = singleSlideContent("First"):
      text("A")

    content.should(include("[1] First"))

  it should "render paragraphs as plain text" in:
    val content = singleSlideContent():
      text("Hello world")

    content.should(include("Hello world"))

  it should "render bullet lists with hyphen prefixes" in:
    val content = singleSlideContent("Bullets"):
      bullets("One", "Two")

    content.should(include("- One") and include("- Two"))

  it should "render code blocks using fenced syntax" in:
    val content = singleSlideContent("Code"):
      code("scala", "val x = 42")

    content.should(
      include("```scala") and
        include("val x = 42") and
        include("```"),
    )

  it should "render layout information" in:
    val content = singleSlideContentWithLayout("Centered", Layout.Centered):
      text("Hello")

    content.should(include("Centered (Centered)"))
