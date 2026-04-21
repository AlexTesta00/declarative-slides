package declslides.cli

object DeclSlidesCli:

  private def program(
    printError: String => Unit,
    printInfo: String => Unit,
  ): CliProgram =
    new CliProgram(
      commandFactory = DefaultRenderCommandFactory,
      printError = printError,
      printInfo = printInfo,
    )

  private def execute(
    args: Array[String],
    printError: String => Unit,
    printInfo: String => Unit,
  ): ExitCode =
    program(
      printError = printError,
      printInfo = printInfo,
    ).run(args)

  def run(
    args: Array[String],
    printError: String => Unit = System.err.println,
    printInfo: String => Unit = println,
  ): Int =
    execute(
      args = args,
      printError = printError,
      printInfo = printInfo,
    ).value

  def main(args: Array[String]): Unit =
    val exitCode =
      execute(
        args = args,
        printError = System.err.println,
        printInfo = println,
      )

    if exitCode != ExitCode.Success then
      sys.exit(exitCode.value)
