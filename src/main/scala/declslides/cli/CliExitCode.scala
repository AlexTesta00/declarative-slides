package declslides.cli

enum CliExitCode(val code: Int) derives CanEqual:
  case Success extends CliExitCode(0)
  case Failure extends CliExitCode(1)
