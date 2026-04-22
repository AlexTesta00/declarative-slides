package declslides.cli

/** Validated command-line configuration for a render invocation.
  *
  * By the time a `CliConfig` exists, option names have already been validated
  * and raw strings have already been converted into domain-friendly values.
  *
  * @param input
  *   input script to render
  * @param format
  *   requested output format
  * @param output
  *   destination file for the rendered document
  */
final case class CliConfig(
  input: os.Path,
  format: OutputFormat,
  output: os.Path)
