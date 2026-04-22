package dsl

import declslides.domain._
import declslides.dsl.DSL._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DslSpec extends AnyFlatSpec with Matchers:

  private def expectRight(
    result: Either[Vector[DomainError], Presentation],
  ): Presentation =
    result match
      case Right(deck) => deck
      case Left(errors) =>
        fail(s"Expected Right(Presentation), got Left($errors)")

  private def expectLeft(
    result: Either[Vector[DomainError], Presentation],
  ): Vector[DomainError] =
    result match
      case Left(errors) => errors
      case Right(deck) => fail(s"Expected Left(errors), got Right($deck)")

  "The DSL" should "build a presentation with one slide" in:
    val result =
      presentation("Demo") {
        deck(
          slide("Intro") {
            content(
              text("Hello"),
            )
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.title shouldBe "Demo"
    deckResult.slides should have size 1
    deckResult.slides.head.title shouldBe "Intro"

  it should "preserve the declaration order of slides" in:
    val result =
      presentation("Demo") {
        deck(
          slide("One")(content(text("1"))),
          slide("Two")(content(text("2"))),
          slide("Three")(content(text("3"))),
        )
      }

    val deckResult = expectRight(result)
    deckResult.slideTitles shouldBe Vector("One", "Two", "Three")

  it should "use the default theme when none is specified" in:
    val result =
      presentation("Demo") {
        deck(
          slide("Intro") {
            content(text("Hello"))
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.theme shouldBe Theme.default

  it should "support themes" in:
    val result =
      presentation("Demo").use(Theme.conference) {
        deck(
          slide("Intro") {
            content(text("Hello"))
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.theme shouldBe Theme.conference

  it should "support bullet lists" in:
    val result =
      presentation("Demo") {
        deck(
          slide("Bullets") {
            content(
              bullets("A", "B", "C"),
            )
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.slides.head.elements shouldBe Vector(
      SlideElement.BulletList(Vector("A", "B", "C")),
    )

  it should "support code blocks" in:
    val result =
      presentation("Demo") {
        deck(
          slide("Code") {
            content(
              code("scala", "val x = 42"),
            )
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.slides.head.elements shouldBe Vector(
      SlideElement.CodeBlock("scala", "val x = 42"),
    )

  it should "support spacers" in:
    val result =
      presentation("Demo") {
        deck(
          slide("Spacing") {
            content(
              text("Before"),
              spacer(2),
              text("After"),
            )
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.slides.head.elements shouldBe Vector(
      SlideElement.Paragraph("Before"),
      SlideElement.Spacer(2),
      SlideElement.Paragraph("After"),
    )

  it should "support custom slide layouts" in:
    val result =
      presentation("Demo") {
        deck(
          slide("Focus", Layout.Centered) {
            content(
              text("Centered"),
            )
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.slides.head.layout shouldBe Layout.Centered

  it should "allow multiple elements inside the same slide" in:
    val result =
      presentation("Demo") {
        deck(
          slide("Mixed") {
            content(
              text("Intro"),
              bullets("One", "Two"),
              code("scala", "println(1)"),
            )
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.slides.head.elements should have size 3

  it should "normalize presentation and slide titles" in:
    val result =
      presentation("  Demo  ") {
        deck(
          slide("  Intro  ") {
            content(
              text("Hello"),
            )
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.title shouldBe "Demo"
    deckResult.slideTitles shouldBe Vector("Intro")

  it should "return an error for an empty presentation title" in:
    val result =
      presentation("   ") {
        deck(
          slide("Intro") {
            content(text("Hello"))
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.EmptyPresentationTitle)

  it should "return an error for a presentation without slides" in:
    val result =
      presentation("Demo") {
        deck()
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.PresentationWithoutSlides)

  it should "return an error for an empty slide title" in:
    val result =
      presentation("Demo") {
        deck(
          slide("   ") {
            content(text("Hello"))
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.EmptySlideTitle)

  it should "return an error for an empty slide" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Oops") {
            content()
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.SlideWithoutElements("Oops"))

  it should "return an error for duplicate slide titles" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Intro")(content(text("A"))),
          slide("Intro")(content(text("B"))),
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.DuplicateSlideTitles(Vector("Intro")))

  it should "detect duplicate slide titles after trimming" in:
    val result =
      presentation("Broken") {
        deck(
          slide(" Intro ")(content(text("A"))),
          slide("Intro")(content(text("B"))),
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.DuplicateSlideTitles(Vector("Intro")))

  it should "return duplicate titles only once and sorted" in:
    val result =
      presentation("Demo") {
        deck(
          slide("B")(content(text("1"))),
          slide("A")(content(text("2"))),
          slide("B")(content(text("3"))),
          slide("A")(content(text("4"))),
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.DuplicateSlideTitles(Vector("A", "B")))

  it should "return an error for an empty paragraph" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Intro") {
            content(
              text("   "),
            )
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.EmptyParagraph)

  it should "return an error for an empty bullet list" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Bullets") {
            content(
              bullets(),
            )
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.EmptyBulletList)

  it should "return an error for an empty bullet item" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Bullets") {
            content(
              bullets("A", "   ", "C"),
            )
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.EmptyBulletItem(1))

  it should "return errors for multiple empty bullet items" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Bullets") {
            content(
              bullets(" ", "ok", "   "),
            )
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.EmptyBulletItem(0))
    errors should contain(DomainError.EmptyBulletItem(2))

  it should "return an error for blank code language" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Code") {
            content(
              code("   ", "println(1)"),
            )
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.EmptyCodeLanguage)

  it should "return an error for blank code source" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Code") {
            content(
              code("scala", "   "),
            )
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.EmptyCodeBlock)

  it should
    "return both code language and code block errors when both are blank" in:
      val result =
        presentation("Broken") {
          deck(
            slide("Code") {
              content(
                code("   ", "   "),
              )
            },
          )
        }

      val errors = expectLeft(result)
      errors should contain(DomainError.EmptyCodeLanguage)
      errors should contain(DomainError.EmptyCodeBlock)

  it should "return an error for a non positive spacer with zero lines" in {
    val result =
      presentation("Broken") {
        deck(
          slide("Spacing") {
            content(
              spacer(0),
            )
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.NonPositiveSpacer(0))
  }

  it should "return an error for a non positive spacer with negative lines" in:
    val result =
      presentation("Broken") {
        deck(
          slide("Spacing") {
            content(
              spacer(-2),
            )
          },
        )
      }

    val errors = expectLeft(result)
    errors should contain(DomainError.NonPositiveSpacer(-2))

  it should
    "accumulate presentation and slide errors instead of failing fast" in:
      val result =
        presentation("   ") {
          deck(
            slide(" Intro ") {
              content()
            },
            slide("Intro") {
              content(
                code("   ", "   "),
              )
            },
            slide("   ") {
              content(
                text("   "),
                spacer(0),
              )
            },
          )
        }

      val errors = expectLeft(result)

      errors should contain(DomainError.EmptyPresentationTitle)
      errors should contain(DomainError.SlideWithoutElements("Intro"))
      errors should contain(DomainError.DuplicateSlideTitles(Vector("Intro")))
      errors should contain(DomainError.EmptyCodeLanguage)
      errors should contain(DomainError.EmptyCodeBlock)
      errors should contain(DomainError.EmptySlideTitle)
      errors should contain(DomainError.EmptyParagraph)
      errors should contain(DomainError.NonPositiveSpacer(0))

  it should
    "return all valid slides in the final presentation when there are no errors" in:
      val result =
        presentation("Demo") {
          deck(
            slide("One") {
              content(text("first"))
            },
            slide("Two") {
              content(
                bullets("a", "b"),
                spacer(),
                code("scala", "println(2)"),
              )
            },
          )
        }

      val deckResult = expectRight(result)
      deckResult.slides.map(_.title) shouldBe Vector("One", "Two")
      deckResult.slides(1).elements shouldBe Vector(
        SlideElement.BulletList(Vector("a", "b")),
        SlideElement.Spacer(1),
        SlideElement.CodeBlock("scala", "println(2)"),
      )

  it should "keep the exact order of elements inside a slide" in {
    val result =
      presentation("Demo") {
        deck(
          slide("Ordered") {
            content(
              text("A"),
              spacer(),
              bullets("B1", "B2"),
              code("scala", "println(3)"),
              text("Z"),
            )
          },
        )
      }

    val deckResult = expectRight(result)
    deckResult.slides.head.elements shouldBe Vector(
      SlideElement.Paragraph("A"),
      SlideElement.Spacer(1),
      SlideElement.BulletList(Vector("B1", "B2")),
      SlideElement.CodeBlock("scala", "println(3)"),
      SlideElement.Paragraph("Z"),
    )
  }
