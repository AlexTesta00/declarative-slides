package declslides.cli

final case class CliConfig(
  input: os.Path,
  format: OutputFormat,
  output: os.Path)
