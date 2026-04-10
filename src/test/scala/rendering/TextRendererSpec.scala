package rendering

import declslides.domain.Layout
import declslides.dsl.DSL._
import declslides.rendering.RenderingTarget
import declslides.rendering.RenderingTarget.Text
import declslides.rendering.TextRenderer
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextRendererSpec extends AnyFlatSpec with Matchers:

  behavior of "TextRenderer"

  private val renderer = TextRenderer()

  private def demoDeck(body: => PresBuild) =
    presentation("Demo") {
      slide("Intro")(text("Hello"))
      body
    }.value

  private def render(body: => PresBuild) =
    renderer.render(demoDeck(body))

  private def renderedContent(body: => PresBuild) =
    render(body).content

  private def singleSlideContent(title: String = "Intro")(body: => SlideBuild)
    : String =
    renderedContent:
      slide(title):
        body

  private def singleSlideContentWithLayout(
    title: String,
    layout: Layout,
  )(body: => SlideBuild,
  ): String =
    renderedContent:
      slide(title, layout):
        body

  it should "return a text document" in:
    val document = render:
      slide("Intro")(text("Hello"))

    document.target shouldBe Text
    document.fileExtension shouldBe "txt"

  it should "render the presentation title" in:
    val content = singleSlideContent():
      text("Hello")

    content should include("Demo")

  it should "render the theme name" in:
    val content = singleSlideContent():
      text("Hello")

    content should include("Theme: default")

  it should "preserve slide order" in:
    val content = renderedContent:
      slide("First")(text("A"))
      slide("Second")(text("B"))

    content.indexOf("First") should be < content.indexOf("Second")

  it should "number slides" in:
    val content = singleSlideContent("First"):
      text("A")

    content should include("[1] First")

  it should "render paragraphs as plain text" in:
    val content = singleSlideContent():
      text("Hello world")

    content should include("Hello world")

  it should "render bullet lists with hyphen prefixes" in:
    val content = singleSlideContent("Bullets"):
      bullets("One", "Two")

    content should (include("- One") and include("- Two"))

  it should "render code blocks using fenced syntax" in:
    val content = singleSlideContent("Code"):
      code("scala", "val x = 42")

    content should (
      include("```scala") and
        include("val x = 42") and
        include("```")
    )

  it should "render layout information" in:
    val content = singleSlideContentWithLayout("Centered", Layout.Centered):
      text("Hello")

    content should include("Centered (Centered)")
