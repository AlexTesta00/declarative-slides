package declslides.cli

enum OutputFormat(val value: String):
  case Html extends OutputFormat("html")
  case Text extends OutputFormat("text")
  case Txt extends OutputFormat("txt")

object OutputFormat:

  val supported: List[OutputFormat] =
    List(
      OutputFormat.Html,
      OutputFormat.Text,
      OutputFormat.Txt,
    )

  private val byValue: Map[String, OutputFormat] =
    supported.map(format => format.value -> format).toMap

  def parse(value: String): Either[CliError, OutputFormat] =
    byValue
      .get(value)
      .toRight(CliError.InvalidFormat(value, supportedValues))

  def supportedValues: List[String] =
    supported.map(_.value)
