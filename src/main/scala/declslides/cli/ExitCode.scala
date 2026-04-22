package declslides.cli

/** Semantic process exit codes used by the CLI.
  *
  * Named exit codes make intent clearer than bare numeric literals.
  */
enum ExitCode(val value: Int):
  /** Successful execution. */
  case Success extends ExitCode(0)

  /** Failed execution. */
  case Failure extends ExitCode(1)
