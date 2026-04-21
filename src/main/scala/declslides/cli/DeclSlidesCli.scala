package declslides.cli

object DeclSlidesCli:

  private def program(
    printError: String => Unit,
  ): CliProgram =
    new CliProgram(
      commandFactory = DefaultRenderCommandFactory,
      printError = printError,
    )

  private def execute(
    args: Array[String],
    printError: String => Unit,
  ): ExitCode =
    program(printError).run(args)

  def run(
    args: Array[String],
    printError: String => Unit = System.err.println,
  ): Int =
    execute(args, printError).value

  def main(args: Array[String]): Unit =
    val exitCode = execute(args, System.err.println)

    if exitCode != ExitCode.Success then
      sys.exit(exitCode.value)
