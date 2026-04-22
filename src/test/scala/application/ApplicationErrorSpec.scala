package application

import declslides.application.ApplicationError
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ApplicationErrorSpec extends AnyFlatSpec with Matchers:

  behavior of "ApplicationError"

  it should "describe an input file not found error" in:
    val error =
      ApplicationError.InputFileNotFound("deck.sc")

    error.message shouldBe "Input file not found: deck.sc"

  it should "describe an unsupported input file error" in:
    val error =
      ApplicationError.UnsupportedInputFile("deck.txt")

    error.message shouldBe
      "Unsupported input file: deck.txt. Expected a .sc script containing a DeclSlides expression"

  it should "describe a cannot read input error" in:
    val error =
      ApplicationError.CannotReadInput(
        path = "deck.sc",
        reason = "permission denied",
      )

    error.message shouldBe "Cannot read input file 'deck.sc': permission denied"

  it should "describe an unsupported format error" in:
    val error =
      ApplicationError.UnsupportedFormat(
        raw = "pdf",
        supported = Vector("html", "text", "txt"),
      )

    error.message shouldBe
      "Unsupported format 'pdf'. Expected one of: html, text, txt"

  it should "describe a scala cli unavailable error" in:
    val error =
      ApplicationError.ScalaCliUnavailable(
        binary = "scala-cli",
        reason = "command not found",
      )

    error.message shouldBe "Cannot execute 'scala-cli': command not found"

  it should "describe a script execution failure" in:
    val error =
      ApplicationError.ScriptExecutionFailed("boom")

    error.message shouldBe "Script execution failed:\nboom"
