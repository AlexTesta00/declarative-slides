package declslides.cli

/** Entry point of the `declslides` command-line application.
  *
  * This object keeps process-level concerns small: it wires the CLI program,
  * delegates the actual work, and turns the final result into a process exit
  * code.
  */
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

  /** Runs the CLI and returns the numeric exit code.
    *
    * This method is intentionally small and test-friendly: callers can override
    * the output functions to capture messages without touching standard
    * streams.
    *
    * @param args
    *   raw command-line arguments
    * @param printError
    *   function used to report user-facing errors
    * @param printInfo
    *   function used to report successful completion messages
    * @return
    *   the process exit code as an `Int`
    */
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
