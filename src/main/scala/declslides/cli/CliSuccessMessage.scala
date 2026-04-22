package declslides.cli

/** Success messages emitted by the CLI.
  *
  * Centralizing user-facing success text keeps the orchestration code focused
  * on flow rather than wording.
  */
object CliSuccessMessage:

  /** Builds the confirmation message shown after a successful render. */
  def rendered(config: CliConfig): String =
    s"Rendered '${config.input.last}' to '${config.output.toString}'"
