package declslides.cli

enum CliOption(val flag: String):
  case Input extends CliOption("--input")
  case Format extends CliOption("--format")
  case Output extends CliOption("--output")

object CliOption:

  val all: List[CliOption] =
    List(
      CliOption.Input,
      CliOption.Format,
      CliOption.Output,
    )

  private val byFlag: Map[String, CliOption] =
    all.map(option => option.flag -> option).toMap

  def parse(flag: String): Either[CliError, CliOption] =
    byFlag.get(flag).toRight(CliError.UnknownOption(flag))
