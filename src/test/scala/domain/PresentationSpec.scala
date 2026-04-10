package domain

import declslides.domain.DomainError
import declslides.domain.Layout
import declslides.domain.Presentation
import declslides.domain.Slide
import declslides.domain.SlideElement
import declslides.domain.Theme
import org.scalatest.EitherValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PresentationSpec extends AnyFlatSpec with Matchers:

  private def paragraph(text: String): SlideElement =
    SlideElement.Paragraph(text)

  private def validSlide(title: String): Slide =
    Slide(title, Vector(paragraph(s"Content for $title"))).value

  "A slide" should "be created when title and elements are valid" in:
    val result = Slide("Intro", Vector(paragraph("Hello")))

    result.isRight shouldBe true
    result.value.title shouldBe "Intro"
    result.value.elements.size shouldBe 1
    result.value.layout shouldBe Layout.Flow

  it should "use the provided layout" in:
    val result =
      Slide(
        "Intro",
        Vector(paragraph("Hello")),
        Layout.Centered,
      )

    result.isRight shouldBe true
    result.value.layout shouldBe Layout.Centered

  it should "trim the title when created" in:
    val result = Slide("  Intro  ", Vector(paragraph("Hello")))

    result.isRight shouldBe true
    result.value.title shouldBe "Intro"

  it should "reject a blank title" in:
    val result = Slide("   ", Vector(paragraph("Hello")))

    result.left.value should contain(DomainError.EmptySlideTitle)

  it should "reject empty elements" in:
    val result = Slide("Empty", Vector.empty)

    result.left.value should contain(
      DomainError.SlideWithoutElements("Empty"),
    )

  it should "reject an empty paragraph" in:
    val result =
      Slide("Paragraph", Vector(SlideElement.Paragraph("   ")))

    result.left.value should contain(DomainError.EmptyParagraph)

  it should "reject an empty bullet list" in:
    val result =
      Slide("Bullets", Vector(SlideElement.BulletList(Vector.empty)))

    result.left.value should contain(DomainError.EmptyBulletList)

  it should "accept a valid bullet list" in:
    val result =
      Slide(
        "Bullets",
        Vector(SlideElement.BulletList(Vector("First", "Second"))),
      )

    result.isRight shouldBe true
    result.value.elements shouldBe Vector(
      SlideElement.BulletList(Vector("First", "Second")),
    )

  it should "reject blank bullet items" in:
    val result =
      Slide(
        "Bullets",
        Vector(SlideElement.BulletList(Vector("First", "   ", "Third"))),
      )

    result.left.value should contain(DomainError.EmptyBulletItem(1))

  it should "reject a blank code language" in:
    val result =
      Slide(
        "Code",
        Vector(SlideElement.CodeBlock("   ", "val x = 42")),
      )

    result.left.value should contain(DomainError.EmptyCodeLanguage)

  it should "reject blank code content" in:
    val result =
      Slide(
        "Code",
        Vector(SlideElement.CodeBlock("scala", "   ")),
      )

    result.left.value should contain(DomainError.EmptyCodeBlock)

  it should "accept a valid code block" in:
    val result =
      Slide(
        "Code",
        Vector(SlideElement.CodeBlock("scala", "val x = 42")),
      )

    result.isRight shouldBe true
    result.value.elements shouldBe Vector(
      SlideElement.CodeBlock("scala", "val x = 42"),
    )

  it should "reject a non-positive spacer" in:
    val result =
      Slide(
        "Spacer",
        Vector(SlideElement.Spacer(0)),
      )

    result.left.value should contain(DomainError.NonPositiveSpacer(0))

  it should "accept a positive spacer" in:
    val result =
      Slide(
        "Spacer",
        Vector(SlideElement.Spacer(2)),
      )

    result.isRight shouldBe true
    result.value.elements shouldBe Vector(
      SlideElement.Spacer(2),
    )

  it should "collect multiple validation errors" in:
    val result =
      Slide(
        "   ",
        Vector(
          SlideElement.Paragraph("   "),
          SlideElement.Spacer(0),
        ),
      )

    result.isLeft shouldBe true
    val errors = result.left.value
    errors should contain(DomainError.EmptySlideTitle)
    errors should contain(DomainError.EmptyParagraph)
    errors should contain(DomainError.NonPositiveSpacer(0))

  it should "report all duplicate slide titles" in:
    val result =
      Presentation(
        "Demo",
        Vector(
          validSlide("Intro"),
          validSlide("Body"),
          validSlide("Intro"),
          validSlide("Body"),
        ),
        Theme.default,
      )

    result.isLeft shouldBe true

  it should "define whether duplicate titles are case-sensitive" in:
    val result =
      Presentation(
        "Demo",
        Vector(validSlide("Intro"), validSlide("intro")),
        Theme.default,
      )

    result.isRight shouldBe true

  "A presentation" should "be created when title and slides are valid" in:
    val result =
      Presentation("Demo", Vector(validSlide("Intro")), Theme.default)

    result.isRight shouldBe true
    result.value.title shouldBe "Demo"
    result.value.slides.map(_.title) shouldBe Vector("Intro")
    result.value.theme shouldBe Theme.default

  it should "preserve slide order" in:
    val result =
      Presentation(
        "Demo",
        Vector(validSlide("Intro"), validSlide("Body"), validSlide("End")),
        Theme.default,
      )

    result.isRight shouldBe true
    result.value.slides.map(_.title) shouldBe Vector("Intro", "Body", "End")

  it should "trim the presentation title" in:
    val result =
      Presentation("  Demo  ", Vector(validSlide("Only")), Theme.default)

    result.isRight shouldBe true
    result.value.title shouldBe "Demo"

  it should "reject an empty presentation title" in:
    val result =
      Presentation("   ", Vector(validSlide("Only")), Theme.default)

    result.left.value should contain(DomainError.EmptyPresentationTitle)

  it should "reject missing slides" in:
    val result = Presentation("Demo", Vector.empty, Theme.default)

    result.left.value should contain(
      DomainError.PresentationWithoutSlides,
    )

  it should "reject duplicate slide titles" in:
    val result =
      Presentation(
        "Demo",
        Vector(validSlide("Intro"), validSlide("Intro")),
        Theme.default,
      )

    result.isLeft shouldBe true
    result.left.value.exists {
      case DomainError.DuplicateSlideTitles(titles) => titles == Vector("Intro")
      case _ => false
    } shouldBe true

  it should "expose slide titles in order" in:
    val deck =
      Presentation(
        "Demo",
        Vector(validSlide("One"), validSlide("Two")),
        Theme.default,
      ).value

    deck.slideTitles shouldBe Vector("One", "Two")

  "Theme.default" should "have the expected name" in:
    Theme.default.name shouldBe "default"
