package declslides.cli

/** Supported command-line options understood by the CLI.
  *
  * Using an enum keeps option handling explicit and avoids scattering raw flag
  * strings across the parser.
  */
enum CliOption(val flag: String):
  /** Input script path. */
  case Input extends CliOption("--input")

  /** Output format label. */
  case Format extends CliOption("--format")

  /** Output format label. */
  case Output extends CliOption("--output")

/** Helpers for working with [[CliOption]] values. */
object CliOption:

  val all: List[CliOption] =
    List(
      CliOption.Input,
      CliOption.Format,
      CliOption.Output,
    )

  private val byFlag: Map[String, CliOption] =
    all.map(option => option.flag -> option).toMap

  /** Parses a raw flag into a supported CLI option.
    *
    * @param flag
    *   raw flag as provided on the command line
    * @return
    *   the matching option, or an error if the flag is unknown
    */
  def parse(flag: String): Either[CliError, CliOption] =
    byFlag.get(flag).toRight(CliError.UnknownOption(flag))
