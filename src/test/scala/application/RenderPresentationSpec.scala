package application

import declslides.application.ApplicationError
import declslides.application.FileSystem
import declslides.application.InMemoryPresentationRegistry
import declslides.application.RenderFormat
import declslides.application.RenderPresentation
import declslides.application.RenderRequest
import declslides.domain.DomainError
import declslides.domain.Presentation
import declslides.dsl.DSL._
import declslides.rendering.Document
import declslides.rendering.RenderingTarget
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RenderPresentationSpec extends AnyFlatSpec with Matchers:

  private def validPresentation(
    result: Either[Vector[DomainError], Presentation],
  ): Presentation =
    result match
      case Right(presentation) => presentation
      case Left(errors) => fail(errors.map(_.message).mkString("; "))

  private val sampleDeck: Presentation =
    validPresentation(
      presentation("Demo") {
        deck(
          slide("Intro") {
            content(
              text("Hello"),
            )
          },
        )
      },
    )

  private final class RecordingFileSystem extends FileSystem:
    var writes: Map[String, String] = Map.empty

    override def write(
      path: String,
      content: String,
    ): Either[ApplicationError, Unit] =
      writes = writes.updated(path, content)
      Right(())

  "An in-memory registry" should
    "list available presentation names in sorted order" in {
      val registry =
        InMemoryPresentationRegistry(
          "zeta" -> sampleDeck,
          "alpha" -> sampleDeck,
        )

      registry.available shouldBe Vector("alpha", "zeta")
    }

  it should "resolve a known presentation" in {
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)

    val result = registry.resolve("demo")

    result.isRight shouldBe true
    result.toOption.get.title shouldBe "Demo"
  }

  it should "fail for an unknown presentation" in {
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)

    registry.resolve("missing") shouldBe Left(
      ApplicationError.PresentationNotFound("missing"),
    )
  }

  "RenderFormat.parse" should "parse html format" in {
    RenderFormat.parse("html") shouldBe Right(RenderFormat.Html)
  }

  it should "parse text format" in {
    RenderFormat.parse("text") shouldBe Right(RenderFormat.Text)
  }

  it should "parse txt as text format" in {
    RenderFormat.parse("txt") shouldBe Right(RenderFormat.Text)
  }

  it should "reject unsupported formats" in {
    val result = RenderFormat.parse("pdf")

    result.isLeft shouldBe true
    result.left.toOption.get shouldBe
      ApplicationError.InvalidCommand(
        "Unsupported format 'pdf'. Expected one of: html, text",
      )
  }

  "RenderPresentation" should
    "render an html document when html is requested" in {
      val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)
      val fs = new RecordingFileSystem
      val service =
        new RenderPresentation(
          registry = registry,
          htmlRenderer = new HtmlRenderer,
          textRenderer = new TextRenderer,
          fileSystem = fs,
        )

      val result = service.run(
        RenderRequest("demo", RenderFormat.Html, None),
      )

      result.isRight shouldBe true
      val renderResult = result.toOption.get
      renderResult.document.target shouldBe RenderingTarget.Html
      renderResult.document.fileExtension shouldBe "html"
      renderResult.document.content should include("<!DOCTYPE html>")
      renderResult.writtenTo shouldBe None
    }

  it should "render a text document when text is requested" in {
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)
    val fs = new RecordingFileSystem
    val service =
      new RenderPresentation(
        registry = registry,
        htmlRenderer = new HtmlRenderer,
        textRenderer = new TextRenderer,
        fileSystem = fs,
      )

    val result = service.run(
      RenderRequest("demo", RenderFormat.Text, None),
    )

    result.isRight shouldBe true
    val renderResult = result.toOption.get
    renderResult.document.target shouldBe RenderingTarget.Text
    renderResult.document.fileExtension shouldBe "txt"
    renderResult.document.content should include("Demo")
    renderResult.writtenTo shouldBe None
  }

  it should "not write when no output path is provided" in {
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)
    val fs = new RecordingFileSystem
    val service =
      new RenderPresentation(
        registry = registry,
        htmlRenderer = new HtmlRenderer,
        textRenderer = new TextRenderer,
        fileSystem = fs,
      )

    val result = service.run(
      RenderRequest("demo", RenderFormat.Text, None),
    )

    result.isRight shouldBe true
    fs.writes shouldBe Map.empty
  }

  it should "write the rendered document when an output path is provided" in {
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)
    val fs = new RecordingFileSystem
    val service =
      new RenderPresentation(
        registry = registry,
        htmlRenderer = new HtmlRenderer,
        textRenderer = new TextRenderer,
        fileSystem = fs,
      )

    val result = service.run(
      RenderRequest("demo", RenderFormat.Text, Some("out/demo.txt")),
    )

    result.isRight shouldBe true
    fs.writes.keySet should contain("out/demo.txt")
    fs.writes("out/demo.txt") should include("Demo")
    result.toOption.get.writtenTo shouldBe Some("out/demo.txt")
  }

  it should "propagate a missing presentation error" in {
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)
    val fs = new RecordingFileSystem
    val service =
      new RenderPresentation(
        registry = registry,
        htmlRenderer = new HtmlRenderer,
        textRenderer = new TextRenderer,
        fileSystem = fs,
      )

    val result = service.run(
      RenderRequest("missing", RenderFormat.Text, None),
    )

    result shouldBe Left(ApplicationError.PresentationNotFound("missing"))
  }

  it should "keep the produced document in the render result" in {
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)
    val fs = new RecordingFileSystem
    val service =
      new RenderPresentation(
        registry = registry,
        htmlRenderer = new HtmlRenderer,
        textRenderer = new TextRenderer,
        fileSystem = fs,
      )

    val result = service.run(
      RenderRequest("demo", RenderFormat.Html, Some("out/demo.html")),
    )

    result.isRight shouldBe true
    val renderResult = result.toOption.get
    renderResult.document shouldBe a[Document]
    renderResult.writtenTo shouldBe Some("out/demo.html")
  }
