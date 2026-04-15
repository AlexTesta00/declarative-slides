package application

import declslides.application.ApplicationError
import declslides.application.RenderRequest
import declslides.rendering.Document
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.EitherValues.convertLeftProjectionToValuable
import org.scalatest.flatspec.AnyFlatSpec

class RenderPresentationSpec extends AnyFlatSpec with ApplicationSpecSupport:

  behavior of "RenderPresentation"

  private val htmlFormat = HtmlRenderer.Target
  private val textFormat = TextRenderer.Target

  it should "render an html document when html is requested" in:
    val (service, _) = this.service("demo" -> sampleDeck)

    val result =
      service.run(
        RenderRequest("demo", htmlFormat, None),
      ).value

    result.document.target.shouldBe(htmlFormat)
    result.document.fileExtension.shouldBe("html")
    result.document.content.should(include("<!DOCTYPE html>"))
    result.writtenTo.shouldBe(None)

  it should "render a text document when text is requested" in:
    val (service, _) = this.service("demo" -> sampleDeck)

    val result =
      service.run(
        RenderRequest("demo", textFormat, None),
      ).value

    result.document.target.shouldBe(textFormat)
    result.document.fileExtension.shouldBe("txt")
    result.document.content.should(include("Demo"))
    result.writtenTo.shouldBe(None)

  it should "not write when no output path is provided" in:
    val (service, fs) = this.service("demo" -> sampleDeck)

    service.run(
      RenderRequest("demo", textFormat, None),
    ).value

    fs.writes.shouldBe(Map.empty)

  it should "write the rendered document when an output path is provided" in:
    val (service, fs) = this.service("demo" -> sampleDeck)

    val result =
      service.run(
        RenderRequest("demo", textFormat, Some("out/demo.txt")),
      ).value

    fs.writes.keySet.should(contain("out/demo.txt"))
    fs.writes("out/demo.txt").should(include("Demo"))
    result.writtenTo.shouldBe(Some("out/demo.txt"))

  it should "propagate a missing presentation error" in:
    val (service, _) = this.service("demo" -> sampleDeck)

    service.run(
      RenderRequest("missing", textFormat, None),
    ).left.value.shouldBe(
      ApplicationError.PresentationNotFound("missing"),
    )

  it should "keep the produced document in the render result" in:
    val (service, _) = this.service("demo" -> sampleDeck)

    val result =
      service.run(
        RenderRequest("demo", htmlFormat, Some("out/demo.html")),
      ).value

    result.document.shouldBe(a[Document])
    result.writtenTo.shouldBe(Some("out/demo.html"))
