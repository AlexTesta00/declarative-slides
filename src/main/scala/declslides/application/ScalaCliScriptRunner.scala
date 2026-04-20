package declslides.application

import declslides.rendering.RenderFormat

import scala.util.control.NonFatal

final class ScalaCliScriptRunner(
  declslidesDependency: String,
  scalaCliBinary: String = "scala-cli",
  scalaVersion: Option[String] = None) extends ScriptRunner:

  override def render(
    input: os.Path,
    target: RenderFormat,
    output: os.Path,
  ): Either[ApplicationError, Unit] =
    for
      _ <- validateInput(input)
      userSource <- readInput(input)
      _ <- executeBootstrap(
        userSource = userSource,
        target = target,
        output = output,
      )
    yield ()

  private def validateInput(input: os.Path): Either[ApplicationError, Unit] =
    if !os.exists(input) then
      Left(ApplicationError.InputFileNotFound(input.toString))
    else if input.ext != "sc" then
      Left(ApplicationError.UnsupportedInputFile(input.toString))
    else
      Right(())

  private def readInput(input: os.Path): Either[ApplicationError, String] =
    try
      Right(os.read(input))
    catch
      case NonFatal(error) =>
        Left(
          ApplicationError.CannotReadInput(
            path = input.toString,
            reason = safeMessage(error),
          ),
        )

  private def executeBootstrap(
    userSource: String,
    target: RenderFormat,
    output: os.Path,
  ): Either[ApplicationError, Unit] =
    val tempDir =
      os.temp.dir(prefix = "declslides-render-")

    val bootstrapFile =
      tempDir / "DeclSlidesRender.scala"

    val outputPath =
      output.toNIO.toAbsolutePath.normalize.toString

    try
      os.write.over(
        bootstrapFile,
        bootstrapSource(userSource),
      )

      val result =
        os.proc(
          scalaCliBinary,
          "run",
          bootstrapFile.toString,
          "--",
          target.label,
          outputPath,
        ).call(
          cwd = tempDir,
          check = false,
          stdout = os.Pipe,
          stderr = os.Pipe,
        )

      Either.cond(
        result.exitCode == 0,
        (),
        ApplicationError.ScriptExecutionFailed(
          formatFailure(result),
        ),
      )
    catch
      case error: java.io.IOException =>
        Left(
          ApplicationError.ScalaCliUnavailable(
            binary = scalaCliBinary,
            reason = safeMessage(error),
          ),
        )

      case NonFatal(error) =>
        Left(
          ApplicationError.ScriptExecutionFailed(
            safeMessage(error),
          ),
        )
    finally
      if os.exists(tempDir) then
        os.remove.all(tempDir)

  private def bootstrapSource(userSource: String): String =
    val scalaDirective =
      scalaVersion.fold("")(version => s"//> using scala $version\n")

    s"""|$scalaDirective//> using dep $declslidesDependency
        |
        |import declslides.dsl.DSL.*
        |import declslides.domain.*
        |import declslides.rendering.DefaultRendererRegistry
        |
        |import java.nio.charset.StandardCharsets
        |import java.nio.file.{Files, Path}
        |
        |object Bootstrap:
        |  def resolve(
        |    result: Either[Vector[DomainError], Presentation],
        |  ): Presentation =
        |    result match
        |      case Right(presentation) =>
        |        presentation
        |
        |      case Left(errors) =>
        |        val renderedErrors =
        |          errors.map(error => s"- $${error.message}").mkString("\\n")
        |
        |        System.err.println(
        |          "Presentation validation failed:\\n" + renderedErrors,
        |        )
        |
        |        sys.exit(1)
        |
        |@main def render(format: String, output: String): Unit =
        |  val resolvedPresentation =
        |    Bootstrap.resolve {
        |${indent(userSource, 6)}
        |    }
        |
        |  val registry =
        |    DefaultRendererRegistry.live
        |
        |  val target =
        |    registry.parse(format).getOrElse {
        |      System.err.println(
        |        "Unsupported format '" + format + "'. Supported formats: " +
        |          registry.supportedLabels.mkString(", "),
        |      )
        |      sys.exit(1)
        |    }
        |
        |  val renderer =
        |    registry.resolve(target).getOrElse {
        |      System.err.println(
        |        "No renderer registered for format '" + target.label + "'",
        |      )
        |      sys.exit(1)
        |    }
        |
        |  val destination =
        |    Path.of(output)
        |
        |  val parent =
        |    destination.getParent
        |
        |  if parent != null then
        |    Files.createDirectories(parent)
        |
        |  Files.writeString(
        |    destination,
        |    renderer.render(resolvedPresentation).content,
        |    StandardCharsets.UTF_8,
        |  )
        |""".stripMargin

  private def indent(
    value: String,
    spaces: Int,
  ): String =
    val padding =
      " " * spaces

    value.linesIterator
      .map(line => s"$padding$line")
      .mkString("\n")

  private def formatFailure(result: os.CommandResult): String =
    val stdout =
      result.out.text().trim

    val stderr =
      result.err.text().trim

    List(
      Option.when(stderr.nonEmpty)(stderr),
      Option.when(stdout.nonEmpty)(stdout),
      Some(s"(exit code: ${result.exitCode})"),
    ).flatten.mkString("\n")

  private def safeMessage(error: Throwable): String =
    Option(error.getMessage)
      .map(_.trim)
      .filter(_.nonEmpty)
      .getOrElse(error.getClass.getSimpleName)
