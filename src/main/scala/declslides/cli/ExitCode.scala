package declslides.cli

enum ExitCode(val value: Int):
  case Success extends ExitCode(0)
  case Failure extends ExitCode(1)
