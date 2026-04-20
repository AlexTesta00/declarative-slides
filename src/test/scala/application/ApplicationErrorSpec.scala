package application

import declslides.application.ApplicationError
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ApplicationErrorSpec extends AnyFlatSpec with Matchers:

  behavior of "ApplicationError"

  it should "render a message for a missing input file" in:
    val error =
      ApplicationError.InputFileNotFound("examples/HelloPresentation.sc")

    error.message shouldBe
      "Input file not found: examples/HelloPresentation.sc"

  it should "render a message for an unsupported input file" in:
    val error =
      ApplicationError.UnsupportedInputFile("examples/HelloPresentation.txt")

    error.message shouldBe
      "Unsupported input file: examples/HelloPresentation.txt. Expected a .sc script containing a DeclSlides expression"

  it should "render a message when the input file cannot be read" in:
    val error =
      ApplicationError.CannotReadInput(
        "examples/HelloPresentation.sc",
        "Access denied",
      )

    error.message shouldBe
      "Cannot read input file 'examples/HelloPresentation.sc': Access denied"

  it should "render a message for an unsupported output format" in:
    val error =
      ApplicationError.UnsupportedFormat("pdf", Vector("html", "text"))

    error.message shouldBe
      "Unsupported format 'pdf'. Expected one of: html, text"

  it should "render a message when scala-cli is unavailable" in:
    val error =
      ApplicationError.ScalaCliUnavailable("scala-cli.bat", "command not found")

    error.message shouldBe
      "Cannot execute 'scala-cli.bat': command not found"

  it should "render a message for script execution failures" in:
    val error =
      ApplicationError.ScriptExecutionFailed("boom")

    error.message shouldBe
      "Script execution failed:\nboom"
