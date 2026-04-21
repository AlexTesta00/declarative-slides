package declslides.cli

object CliSuccessMessage:

  def rendered(config: CliConfig): String =
    s"Rendered '${config.input.last}' to '${config.output.toString}'"
