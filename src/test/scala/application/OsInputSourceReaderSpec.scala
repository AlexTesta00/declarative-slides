package application

import declslides.application.ApplicationError
import declslides.application.OsInputSourceReader
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OsInputSourceReaderSpec extends AnyFlatSpec with Matchers:

  behavior of "OsInputSourceReader"

  it should "read the full source from an input file" in:
    withTempDir { tempDir =>
      val input = tempDir / "deck.sc"
      val source =
        """Right(
          |  Presentation(Vector.empty)
          |)
          |""".stripMargin

      os.write.over(input, source)

      val result =
        OsInputSourceReader.read(input)

      result shouldBe Right(source)
    }

  it should
    "fail with a cannot read input error when the path is not readable as a file" in:
      withTempDir { tempDir =>
        val input = tempDir / "deck.sc"
        os.makeDir(input)

        val result =
          OsInputSourceReader.read(input)

        result.isLeft shouldBe true

        result.left.foreach {
          case ApplicationError.CannotReadInput(path, reason) =>
            path shouldBe input.toString
            reason.trim should not be empty

          case other =>
            fail(s"Unexpected error: $other")
        }
      }

  private def withTempDir(testCode: os.Path => Any): Unit =
    val tempDir =
      os.temp.dir(prefix = "declslides-reader-spec-")

    try testCode(tempDir)
    finally if os.exists(tempDir) then os.remove.all(tempDir)
