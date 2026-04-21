package application

import declslides.application.RenderRequest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RenderRequestSpec extends AnyFlatSpec with Matchers:

  behavior of "RenderRequest"

  it should "store input, requested format and output explicitly" in:
    val request =
      RenderRequest(
        input = os.Path("deck.sc", os.pwd),
        requestedFormat = "html",
        output = os.Path("deck.html", os.pwd),
      )

    request.input shouldBe os.Path("deck.sc", os.pwd)
    request.requestedFormat shouldBe "html"
    request.output shouldBe os.Path("deck.html", os.pwd)

  it should "support value equality" in:
    val left =
      RenderRequest(
        input = os.Path("deck.sc", os.pwd),
        requestedFormat = "text",
        output = os.Path("deck.txt", os.pwd),
      )

    val right =
      RenderRequest(
        input = os.Path("deck.sc", os.pwd),
        requestedFormat = "text",
        output = os.Path("deck.txt", os.pwd),
      )

    left shouldBe right
