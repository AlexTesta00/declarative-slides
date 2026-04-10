package rendering

import declslides.dsl.DSL._
import declslides.rendering.RenderingTarget
import declslides.rendering.TextRenderer
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextRendererSpec extends AnyFlatSpec with Matchers:

  private val renderer = TextRenderer()

  "A text renderer" should "return a text document" in:
    val deck =
      presentation("Demo") {
        slide("Intro")(text("Hello"))
      }.value

    val document = renderer.render(deck)

    document.target shouldBe RenderingTarget.Text
    document.fileExtension shouldBe "txt"

  it should "render the presentation title" in:
    val deck =
      presentation("Demo") {
        slide("Intro")(text("Hello"))
      }.value

    val content = renderer.render(deck).content
    content should include("Demo")

  it should "render the theme name" in:
    val deck =
      presentation("Demo") {
        slide("Intro")(text("Hello"))
      }.value

    val content = renderer.render(deck).content
    content should include("Theme: default")

  it should "preserve slide order" in:
    val deck =
      presentation("Demo") {
        slide("First")(text("A"))
        slide("Second")(text("B"))
      }.value

    val content = renderer.render(deck).content
    content.indexOf("First") should be < content.indexOf("Second")

  it should "number slides" in:
    val deck =
      presentation("Demo") {
        slide("First")(text("A"))
      }.value

    val content = renderer.render(deck).content
    content should include("[1] First")

  it should "render paragraphs as plain text" in:
    val deck =
      presentation("Demo") {
        slide("Intro")(text("Hello world"))
      }.value

    val content = renderer.render(deck).content
    content should include("Hello world")

  it should "render bullet lists with hyphen prefixes" in:
    val deck =
      presentation("Demo") {
        slide("Bullets")(bullets("One", "Two"))
      }.value

    val content = renderer.render(deck).content
    content should include("- One")
    content should include("- Two")

  it should "render code blocks using fenced syntax" in:
    val deck =
      presentation("Demo") {
        slide("Code")(code("scala", "val x = 42"))
      }.value

    val content = renderer.render(deck).content
    content should include("```scala")
    content should include("val x = 42")
    content should include("```")

  it should "render layout information" in:
    val deck =
      presentation("Demo") {
        slide("Centered", declslides.domain.Layout.Centered) {
          text("Hello")
        }
      }.value

    val content = renderer.render(deck).content
    content should include("Centered (Centered)")
