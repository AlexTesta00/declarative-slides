package declslides.cli

import declslides.application.RenderCommand

final class CliProgram(
  commandFactory: RenderCommandFactory,
  printError: String => Unit,
  printInfo: String => Unit):

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
