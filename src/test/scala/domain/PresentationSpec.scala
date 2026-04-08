package domain

import declslides.domain.DomainError
import declslides.domain.Layout
import declslides.domain.Presentation
import declslides.domain.Slide
import declslides.domain.SlideElement
import declslides.domain.Theme
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PresentationSpec extends AnyFlatSpec with Matchers:

  private def paragraph(text: String): SlideElement =
    SlideElement.Paragraph(text)

  private def validSlide(title: String): Slide =
    Slide
      .create(title, Vector(paragraph(s"Content for $title")))
      .toOption
      .get

  "A slide" should "be created when title and elements are valid" in {
    val result = Slide.create("Intro", Vector(paragraph("Hello")))

    result.isRight shouldBe true
    result.toOption.get.title shouldBe "Intro"
    result.toOption.get.elements.size shouldBe 1
    result.toOption.get.layout shouldBe Layout.Flow
  }

  it should "trim the title when created" in {
    val result = Slide.create("  Intro  ", Vector(paragraph("Hello")))

    result.isRight shouldBe true
    result.toOption.get.title shouldBe "Intro"
  }

  it should "reject a blank title" in {
    val result = Slide.create("   ", Vector(paragraph("Hello")))

    result.left.toOption.get should contain(DomainError.EmptySlideTitle)
  }

  it should "reject empty elements" in {
    val result = Slide.create("Empty", Vector.empty)

    result.left.toOption.get should contain(
      DomainError.SlideWithoutElements("Empty")
    )
  }

  it should "reject an empty paragraph" in {
    val result =
      Slide.create("Paragraph", Vector(SlideElement.Paragraph("   ")))

    result.left.toOption.get should contain(DomainError.EmptyParagraph)
  }

  it should "reject an empty bullet list" in {
    val result =
      Slide.create("Bullets", Vector(SlideElement.BulletList(Vector.empty)))

    result.left.toOption.get should contain(DomainError.EmptyBulletList)
  }

  it should "reject blank bullet items" in {
    val result =
      Slide.create(
        "Bullets",
        Vector(SlideElement.BulletList(Vector("First", "   ", "Third")))
      )

    result.left.toOption.get should contain(DomainError.EmptyBulletItem(1))
  }

  it should "reject a blank code language" in {
    val result =
      Slide.create(
        "Code",
        Vector(SlideElement.CodeBlock("   ", "val x = 42"))
      )

    result.left.toOption.get should contain(DomainError.EmptyCodeLanguage)
  }

  it should "reject blank code content" in {
    val result =
      Slide.create(
        "Code",
        Vector(SlideElement.CodeBlock("scala", "   "))
      )

    result.left.toOption.get should contain(DomainError.EmptyCodeBlock)
  }

  it should "reject a non-positive spacer" in {
    val result =
      Slide.create(
        "Spacer",
        Vector(SlideElement.Spacer(0))
      )

    result.left.toOption.get should contain(DomainError.NonPositiveSpacer(0))
  }

  "A presentation" should "be created when title and slides are valid" in {
    val result =
      Presentation.create("Demo", Vector(validSlide("Intro")), Theme.default)

    result.isRight shouldBe true
    result.toOption.get.title shouldBe "Demo"
    result.toOption.get.slides.map(_.title) shouldBe Vector("Intro")
    result.toOption.get.theme shouldBe Theme.default
  }

  it should "preserve slide order" in {
    val result =
      Presentation.create(
        "Demo",
        Vector(validSlide("Intro"), validSlide("Body"), validSlide("End")),
        Theme.default
      )

    result.isRight shouldBe true
    result.toOption.get.slides
      .map(_.title) shouldBe Vector("Intro", "Body", "End")
  }

  it should "trim the presentation title" in {
    val result =
      Presentation.create("  Demo  ", Vector(validSlide("Only")), Theme.default)

    result.isRight shouldBe true
    result.toOption.get.title shouldBe "Demo"
  }

  it should "reject an empty presentation title" in {
    val result =
      Presentation.create("   ", Vector(validSlide("Only")), Theme.default)

    result.left.toOption.get should contain(DomainError.EmptyPresentationTitle)
  }

  it should "reject missing slides" in {
    val result = Presentation.create("Demo", Vector.empty, Theme.default)

    result.left.toOption.get should contain(
      DomainError.PresentationWithoutSlides
    )
  }

  it should "reject duplicate slide titles" in {
    val result =
      Presentation.create(
        "Demo",
        Vector(validSlide("Intro"), validSlide("Intro")),
        Theme.default
      )

    result.isLeft shouldBe true
    result.left.toOption.get.exists {
      case DomainError.DuplicateSlideTitles(titles) => titles == Vector("Intro")
      case _                                        => false
    } shouldBe true
  }

  it should "expose slide titles in order" in {
    val deck =
      Presentation
        .create(
          "Demo",
          Vector(validSlide("One"), validSlide("Two")),
          Theme.default
        )
        .toOption
        .get

    deck.slideTitles shouldBe Vector("One", "Two")
  }

  "Theme.default" should "have the expected name" in {
    Theme.default.name shouldBe "default"
  }
