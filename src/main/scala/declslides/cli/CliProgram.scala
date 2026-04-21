package declslides.cli

import declslides.application.RenderCommand

final class CliProgram(
  commandFactory: RenderCommandFactory,
  printError: String => Unit):

  def run(args: Array[String]): ExitCode =
    execute(args) match
      case Left(error) =>
        printError(s"[error] ${error.message}")
        ExitCode.Failure

      case Right(_) =>
        ExitCode.Success

  private def execute(args: Array[String]): Either[CliError, Unit] =
    for
      config <- CliArgumentParser.parse(args)
      command <- commandFactory.create()
      _ <- render(command, config)
    yield ()

  private def render(
    command: RenderCommand,
    config: CliConfig,
  ): Either[CliError, Unit] =
    command
      .run(
        input = config.input,
        format = config.format.value,
        output = config.output,
      )
      .left
      .map(error => CliError.RenderFailure(error.message))
      .map(_ => ())
