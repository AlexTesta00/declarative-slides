package application

import declslides.application.ApplicationError
import declslides.application.ScalaCliScriptRunner
import declslides.rendering.RenderFormat
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import scala.jdk.CollectionConverters._

class ScalaCliScriptRunnerSpec extends AnyFlatSpec with Matchers:

  behavior of "ScalaCliScriptRunner"

  private val htmlTarget =
    RenderFormat(
      label = "html",
      fileExtension = "html",
      acceptedInputs = Set("html"),
    )

  private def expectLeft[A, B](result: Either[A, B]): A =
    result match
      case Left(error) => error
      case Right(value) => fail(s"Expected Left(error), got Right($value)")

  private def expectRight[A, B](result: Either[A, B]): B =
    result match
      case Right(value) => value
      case Left(error) => fail(s"Expected Right(value), got Left($error)")

  private def isWindows: Boolean =
    System.getProperty("os.name").toLowerCase.contains("win")

  private def writeInputScript(
    dir: os.Path,
    extension: String = "sc",
  ): os.Path =
    val input =
      dir / s"HelloPresentation.$extension"

    os.write(
      input,
      """import declslides.domain.Theme
        |import declslides.dsl.DSL.*
        |
        |presentation("Demo") {
        |  deck(
        |    theme(Theme.conference),
        |    slide("Intro") {
        |      content(
        |        text("Hello"),
        |      )
        |    },
        |  )
        |}
        |""".stripMargin,
    )

    input

  private def makeExecutable(path: os.Path): Unit =
    if !isWindows then
      val permissions =
        Set(
          PosixFilePermission.OWNER_READ,
          PosixFilePermission.OWNER_WRITE,
          PosixFilePermission.OWNER_EXECUTE,
          PosixFilePermission.GROUP_READ,
          PosixFilePermission.GROUP_EXECUTE,
          PosixFilePermission.OTHERS_READ,
          PosixFilePermission.OTHERS_EXECUTE,
        ).asJava

      Files.setPosixFilePermissions(path.toNIO, permissions)

  private def successfulFakeScalaCli(dir: os.Path): String =
    if isWindows then
      val file =
        dir / "scala-cli.bat"

      os.write(
        file,
        """@echo off
          |set output=%~5
          |> "%output%" echo rendered by fake scala-cli
          |exit /b 0
          |""".stripMargin,
      )

      file.toString
    else
      val file =
        dir / "scala-cli"

      os.write(
        file,
        """#!/usr/bin/env sh
          |output="$5"
          |printf "rendered by fake scala-cli" > "$output"
          |exit 0
          |""".stripMargin,
      )

      makeExecutable(file)
      file.toString

  private def failingFakeScalaCli(dir: os.Path): String =
    if isWindows then
      val file =
        dir / "scala-cli.bat"

      os.write(
        file,
        """@echo off
          |echo fake stdout
          |echo fake stderr 1>&2
          |exit /b 7
          |""".stripMargin,
      )

      file.toString
    else
      val file =
        dir / "scala-cli"

      os.write(
        file,
        """#!/usr/bin/env sh
          |echo "fake stdout"
          |echo "fake stderr" >&2
          |exit 7
          |""".stripMargin,
      )

      makeExecutable(file)
      file.toString

  it should "return an error when the input file does not exist" in:
    val tempDir =
      os.temp.dir(prefix = "declslides-runner-test-")

    try
      val runner =
        ScalaCliScriptRunner(
          declslidesDependency = "com.alextesta::declslides:0.1.0-SNAPSHOT",
          scalaCliBinary = "scala-cli",
        )

      val missingInput =
        tempDir / "Missing.sc"

      val result =
        runner.render(
          input = missingInput,
          target = htmlTarget,
          output = tempDir / "out.html",
        )

      result shouldBe Left(
        ApplicationError.InputFileNotFound(missingInput.toString),
      )
    finally
      os.remove.all(tempDir)

  it should "return an error when the input file is not a scala script" in:
    val tempDir =
      os.temp.dir(prefix = "declslides-runner-test-")

    try
      val runner =
        ScalaCliScriptRunner(
          declslidesDependency = "com.alextesta::declslides:0.1.0-SNAPSHOT",
          scalaCliBinary = "scala-cli",
        )

      val input =
        writeInputScript(tempDir, extension = "txt")

      val result =
        runner.render(
          input = input,
          target = htmlTarget,
          output = tempDir / "out.html",
        )

      result shouldBe Left(
        ApplicationError.UnsupportedInputFile(input.toString),
      )
    finally
      os.remove.all(tempDir)

  it should "return an error when scala-cli cannot be executed" in:
    val tempDir =
      os.temp.dir(prefix = "declslides-runner-test-")

    try
      val input =
        writeInputScript(tempDir)

      val runner =
        ScalaCliScriptRunner(
          declslidesDependency = "com.alextesta::declslides:0.1.0-SNAPSHOT",
          scalaCliBinary = "definitely-missing-scala-cli-command-xyz",
        )

      val error =
        expectLeft(
          runner.render(
            input = input,
            target = htmlTarget,
            output = tempDir / "out.html",
          ),
        )

      error shouldBe a[ApplicationError.ScalaCliUnavailable]
      error.message should
        include("Cannot execute 'definitely-missing-scala-cli-command-xyz'")
    finally
      os.remove.all(tempDir)

  it should
    "return a script execution failure when the subprocess exits with a non-zero code" in:
      val tempDir =
        os.temp.dir(prefix = "declslides-runner-test-")

      try
        val input =
          writeInputScript(tempDir)

        val runner =
          ScalaCliScriptRunner(
            declslidesDependency = "com.alextesta::declslides:0.1.0-SNAPSHOT",
            scalaCliBinary = failingFakeScalaCli(tempDir),
          )

        val error =
          expectLeft(
            runner.render(
              input = input,
              target = htmlTarget,
              output = tempDir / "out.html",
            ),
          )

        error shouldBe a[ApplicationError.ScriptExecutionFailed]
        error.message should include("fake stderr")
        error.message should include("fake stdout")
        error.message should include("exit code: 7")
      finally
        os.remove.all(tempDir)

  it should
    "return success when the subprocess exits with zero and writes the output file" in:
      val tempDir =
        os.temp.dir(prefix = "declslides-runner-test-")

      try
        val input =
          writeInputScript(tempDir)

        val output =
          tempDir / "HelloPresentation.html"

        val runner =
          ScalaCliScriptRunner(
            declslidesDependency = "com.alextesta::declslides:0.1.0-SNAPSHOT",
            scalaCliBinary = successfulFakeScalaCli(tempDir),
          )

        val result =
          runner.render(
            input = input,
            target = htmlTarget,
            output = output,
          )

        expectRight(result)
        os.exists(output) shouldBe true
        os.read(output).trim shouldBe "rendered by fake scala-cli"
      finally
        os.remove.all(tempDir)
