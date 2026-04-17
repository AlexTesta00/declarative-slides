package cli

import declslides.application.ApplicationError
import declslides.cli.JvmFileSystem
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.EitherValues.convertLeftProjectionToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.jdk.CollectionConverters._

class JvmFileSystemSpec extends AnyFlatSpec with Matchers:

  behavior of "JvmFileSystem"

  private val fileSystem = new JvmFileSystem

  private def withTempDirectory[A](test: Path => A): A =
    val directory = Files.createTempDirectory("declslides-jvm-fs-spec")
    try
      test(directory)
    finally
      deleteRecursively(directory)

  private def deleteRecursively(path: Path): Unit =
    if Files.exists(path) then
      Files
        .walk(path)
        .iterator()
        .asScala
        .toVector
        .sortBy(_.getNameCount)
        .reverse
        .foreach(Files.deleteIfExists)

  it should "write content to a file" in:
    withTempDirectory { directory =>
      val file = directory.resolve("demo.txt")
      val content = "Hello, file system!"

      val result = fileSystem.write(file.toString, content)

      result.value.shouldBe(())
      Files.exists(file).shouldBe(true)
      Files.readString(file, StandardCharsets.UTF_8).shouldBe(content)
    }

  it should "create parent directories when they do not exist" in:
    withTempDirectory { directory =>
      val file =
        directory
          .resolve("nested")
          .resolve("slides")
          .resolve("demo.txt")

      val content = "Nested output"

      val result = fileSystem.write(file.toString, content)

      result.value.shouldBe(())
      Files.exists(file.getParent).shouldBe(true)
      Files.exists(file).shouldBe(true)
      Files.readString(file, StandardCharsets.UTF_8).shouldBe(content)
    }

  it should "overwrite an existing file" in:
    withTempDirectory { directory =>
      val file = directory.resolve("demo.txt")

      Files.writeString(file, "Old content", StandardCharsets.UTF_8)

      val result = fileSystem.write(file.toString, "New content")

      result.value.shouldBe(())
      Files.readString(file, StandardCharsets.UTF_8).shouldBe("New content")
    }

  it should "return a write failure when the target path is a directory" in:
    withTempDirectory { directory =>
      val error =
        fileSystem.write(directory.toString, "Cannot write here").left.value

      error.shouldBe(a[ApplicationError.WriteFailure])
    }

  it should "report the failing path when writing fails" in:
    withTempDirectory { directory =>
      val error =
        fileSystem.write(directory.toString, "Cannot write here").left.value

      error match
        case ApplicationError.WriteFailure(path, reason) =>
          path.shouldBe(directory.toString)
          reason.nonEmpty.shouldBe(true)

        case other =>
          fail(s"Expected WriteFailure, found: $other")
    }
