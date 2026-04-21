package application

import declslides.application.ApplicationError
import declslides.application.DefaultInputScriptValidator
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DefaultInputScriptValidatorSpec extends AnyFlatSpec with Matchers:

  behavior of "DefaultInputScriptValidator"

  it should "accept an existing scala script file" in:
    withTempDir { tempDir =>
      val input = tempDir / "deck.sc"
      os.write.over(input, "Right(Presentation(Vector.empty))")

      val result =
        DefaultInputScriptValidator.validate(input)

      result shouldBe Right(())
    }

  it should "fail when the input file does not exist" in:
    withTempDir { tempDir =>
      val input = tempDir / "missing.sc"

      val result =
        DefaultInputScriptValidator.validate(input)

      result shouldBe Left(
        ApplicationError.InputFileNotFound(input.toString),
      )
    }

  it should "fail when the input file extension is not supported" in:
    withTempDir { tempDir =>
      val input = tempDir / "deck.txt"
      os.write.over(input, "not a scala script")

      val result =
        DefaultInputScriptValidator.validate(input)

      result shouldBe Left(
        ApplicationError.UnsupportedInputFile(input.toString),
      )
    }

  private def withTempDir(testCode: os.Path => Any): Unit =
    val tempDir =
      os.temp.dir(prefix = "declslides-validator-spec-")

    try testCode(tempDir)
    finally if os.exists(tempDir) then os.remove.all(tempDir)
