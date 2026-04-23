package rendering

import declslides.dsl.DSL._
import declslides.rendering.text.TextRenderer
import org.scalatest.flatspec.AnyFlatSpec

class TextRendererSpec extends AnyFlatSpec with RendererSpecSupport:

  behavior of "TextRenderer"

  override protected val renderer: TextRenderer.type = TextRenderer

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

  it should "render paragraphs as plain text" in:
    val content = singleSlideContent():
      text("Hello")

    content.should(include("Hello"))

  it should "render bullet lists with dash prefixes" in:
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

  it should "render images using a markdown-like image syntax" in:
    val content = singleSlideContent("Media"):
      image("./images/logo.png", "Company logo")

    content should include("![Company logo](./images/logo.png)")
