package declslides.application

import declslides.rendering.RenderFormat

import scala.util.control.NonFatal

/** [[ScriptRunner]] implementation that evaluates presentation scripts with
  * Scala CLI.
  *
  * This runner validates the input, reads the user source, generates a small
  * bootstrap program, executes it through Scala CLI, and cleans up the
  * temporary workspace afterward.
  *
  * @param declslidesDependency
  *   dependency coordinate used inside the generated bootstrap script
  * @param scalaCliBinary
  *   binary used to invoke Scala CLI
  * @param scalaVersion
  *   optional Scala version pin for the bootstrap script
  * @param inputValidator
  *   validator for the input script path
  * @param inputReader
  *   reader for the input script source
  * @param bootstrapSourceFactory
  *   factory used to generate the bootstrap script
  */
final class ScalaCliScriptRunner(
  declslidesDependency: String,
  scalaCliBinary: String = ScalaCliScriptRunner.DefaultScalaCliBinary,
  scalaVersion: Option[String] = None,
  inputValidator: InputScriptValidator = DefaultInputScriptValidator,
  inputReader: InputSourceReader = OsInputSourceReader,
  bootstrapSourceFactory: BootstrapSourceFactory =
    ScalaCliBootstrapSourceFactory) extends ScriptRunner:

  override def render(
    input: os.Path,
    target: RenderFormat,
    output: os.Path,
  ): Either[ApplicationError, Unit] =
    for
      _ <- inputValidator.validate(input)
      userSource <- inputReader.read(input)
      bootstrapSource = bootstrapSourceFactory.create(
        userSource = userSource,
        declslidesDependency = declslidesDependency,
        scalaVersion = scalaVersion,
      )
      _ <- executeBootstrap(
        bootstrapSource = bootstrapSource,
        target = target,
        output = output,
      )
    yield ()

  private def executeBootstrap(
    bootstrapSource: String,
    target: RenderFormat,
    output: os.Path,
  ): Either[ApplicationError, Unit] =
    val workspace =
      createWorkspace()

    try
      writeBootstrapFile(
        bootstrapFile = workspace.bootstrapFile,
        bootstrapSource = bootstrapSource,
      )

      val result =
        invokeScalaCli(
          bootstrapFile = workspace.bootstrapFile,
          target = target,
          output = output,
          workingDirectory = workspace.directory,
        )

      toExecutionResult(result)
    catch
      case error: java.io.IOException =>
        Left(
          ApplicationError.ScalaCliUnavailable(
            binary = scalaCliBinary,
            reason = ErrorMessage(error),
          ),
        )

      case NonFatal(error) =>
        Left(
          ApplicationError.ScriptExecutionFailed(
            ErrorMessage(error),
          ),
        )
    finally
      cleanupWorkspace(workspace)

  private def createWorkspace(): ScalaCliScriptRunner.BootstrapWorkspace =
    val directory =
      os.temp.dir(
        prefix = ScalaCliScriptRunner.TempDirectoryPrefix,
      )

    ScalaCliScriptRunner.BootstrapWorkspace(
      directory = directory,
      bootstrapFile = directory / ScalaCliScriptRunner.BootstrapFileName,
    )

  private def writeBootstrapFile(
    bootstrapFile: os.Path,
    bootstrapSource: String,
  ): Unit =
    os.write.over(
      bootstrapFile,
      bootstrapSource,
    )

  private def invokeScalaCli(
    bootstrapFile: os.Path,
    target: RenderFormat,
    output: os.Path,
    workingDirectory: os.Path,
  ): os.CommandResult =
    os.proc(
      scalaCliBinary,
      ScalaCliScriptRunner.RunSubcommand,
      bootstrapFile.toString,
      ScalaCliScriptRunner.ArgumentSeparator,
      target.label,
      normalizedOutputPath(output),
    ).call(
      cwd = workingDirectory,
      check = false,
      stdout = os.Pipe,
      stderr = os.Pipe,
    )

  private def normalizedOutputPath(
    output: os.Path,
  ): String =
    output.toNIO.toAbsolutePath.normalize.toString

  private def toExecutionResult(
    result: os.CommandResult,
  ): Either[ApplicationError, Unit] =
    Either.cond(
      test = result.exitCode == ScalaCliScriptRunner.SuccessExitCode,
      right = (),
      left = ApplicationError.ScriptExecutionFailed(
        formatFailure(result),
      ),
    )

  private def formatFailure(
    result: os.CommandResult,
  ): String =
    val stdout =
      result.out.text().trim

    val stderr =
      result.err.text().trim

    List(
      Option.when(stderr.nonEmpty)(stderr),
      Option.when(stdout.nonEmpty)(stdout),
      Some(s"(exit code: ${result.exitCode})"),
    ).flatten.mkString("\n")

  private def cleanupWorkspace(
    workspace: ScalaCliScriptRunner.BootstrapWorkspace,
  ): Unit =
    if os.exists(workspace.directory) then
      os.remove.all(workspace.directory)

/** Constants and small internal models used by [[ScalaCliScriptRunner]]. */
object ScalaCliScriptRunner:

  private val DefaultScalaCliBinary = "scala-cli"
  private val TempDirectoryPrefix = "declslides-render-"
  private val BootstrapFileName = "DeclSlidesRender.scala"
  private val RunSubcommand = "run"
  private val ArgumentSeparator = "--"
  private val SuccessExitCode = 0

  private final case class BootstrapWorkspace(
    directory: os.Path,
    bootstrapFile: os.Path)
