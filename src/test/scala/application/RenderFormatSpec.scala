package application

import declslides.application.ApplicationError
import declslides.application.RenderFormat
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.EitherValues.convertLeftProjectionToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RenderFormatSpec extends AnyFlatSpec with Matchers:

  behavior of "RenderFormat.parse"

  it should "parse html format" in:
    RenderFormat.parse("html").value.shouldBe(RenderFormat.Html)

  it should "parse text format" in:
    RenderFormat.parse("text").value.shouldBe(RenderFormat.Text)

  it should "parse txt as text format" in:
    RenderFormat.parse("txt").value.shouldBe(RenderFormat.Text)

  it should "reject unsupported formats" in:
    RenderFormat.parse("pdf").left.value.shouldBe(
      ApplicationError.InvalidCommand(
        "Unsupported format 'pdf'. Expected one of: html, text",
      ),
    )
