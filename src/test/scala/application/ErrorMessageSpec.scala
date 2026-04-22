package application

import declslides.application.ErrorMessage
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ErrorMessageSpec extends AnyFlatSpec with Matchers:

  behavior of "ErrorMessage"

  it should "return a trimmed non blank throwable message" in:
    val error =
      RuntimeException("  something went wrong  ")

    ErrorMessage(error) shouldBe "something went wrong"

  it should
    "fallback to the throwable simple class name when the message is blank" in {
      val error =
        RuntimeException("   ")

      ErrorMessage(error) shouldBe "RuntimeException"
    }

  it should
    "fallback to the throwable simple class name when the message is null" in {
      val error =
        new RuntimeException()

      ErrorMessage(error) shouldBe "RuntimeException"
    }
