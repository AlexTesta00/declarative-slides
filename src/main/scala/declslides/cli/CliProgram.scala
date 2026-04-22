package declslides.cli

import declslides.application.RenderCommand

/** Orchestrates the command-line flow for `declslides`.
  *
  * A `CliProgram` translates raw arguments into a validated configuration,
  * builds the rendering command, executes it, and reports either a friendly
  * error or a success message.
  *
  * @param commandFactory
  *   factory used to build the concrete rendering command
  * @param printError
  *   sink for user-facing error messages
  * @param printInfo
  *   sink for user-facing informational messages
  */
final class CliProgram(
  commandFactory: RenderCommandFactory,
  printError: String => Unit,
  printInfo: String => Unit):

  /** Runs the CLI program with the provided arguments.
    *
    * @param args
    *   raw command-line arguments
    * @return
    *   the semantic exit code of the execution
    */
  def run(args: Array[String]): ExitCode =
    execute(args) match
      case Left(error) =>
        printError(s"[error] ${error.message}")
        ExitCode.Failure

      case Right(message) =>
        printInfo(message)
        ExitCode.Success

  private def execute(args: Array[String]): Either[CliError, String] =
    for
      config <- CliArgumentParser.parse(args)
      command <- commandFactory.create()
      message <- render(command, config)
    yield message

  private def render(
    command: RenderCommand,
    config: CliConfig,
  ): Either[CliError, String] =
    command
      .run(
        input = config.input,
        format = config.format.value,
        output = config.output,
      )
      .left
      .map(error => CliError.RenderFailure(error.message))
      .map(_ => CliSuccessMessage.rendered(config))
