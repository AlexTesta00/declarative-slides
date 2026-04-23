package declslides.cli

/** Output formats accepted by the CLI.
  *
  * This enum models the user-facing format vocabulary accepted on the command
  * line. It intentionally stays small and explicit.
  */
enum OutputFormat(val value: String):
  /** HTML document output. */
  case Html extends OutputFormat("html")

  /** Plain text output using the canonical `text` label. */
  case Text extends OutputFormat("text")

  /** Plain text output using the short `txt` label. */
  case Txt extends OutputFormat("txt")

  /** Markdown output using the canonical `Markdown` label. */
  case Markdown extends OutputFormat("markdown")

  /** Markdown output using the short `md` label. */
  case Md extends OutputFormat("md")

/** Helpers for parsing and listing supported output formats. */
object OutputFormat:

  val supported: List[OutputFormat] =
    List(
      OutputFormat.Html,
      OutputFormat.Text,
      OutputFormat.Txt,
      OutputFormat.Markdown,
      OutputFormat.Md,
    )

  private val byValue: Map[String, OutputFormat] =
    supported.map(format => format.value -> format).toMap

  /** Parses a raw format label into a supported output format. */
  def parse(value: String): Either[CliError, OutputFormat] =
    byValue
      .get(value)
      .toRight(CliError.InvalidFormat(value, supportedValues))

  /** Returns the string labels accepted by the CLI. */
  def supportedValues: List[String] =
    supported.map(_.value)
