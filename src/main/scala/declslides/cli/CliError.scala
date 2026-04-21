package declslides.cli

sealed trait CliError:
  def message: String

object CliError:

  final case class MissingOption(option: CliOption) extends CliError:

    override val message: String =
      s"""Missing required option: ${option.flag}
         |${CliUsage.text}""".stripMargin

  final case class MissingValue(option: String) extends CliError:

    override val message: String =
      s"""Missing value for option: $option
         |${CliUsage.text}""".stripMargin

  final case class UnknownOption(option: String) extends CliError:

    override val message: String =
      s"""Unknown option: $option
         |${CliUsage.text}""".stripMargin

  final case class DuplicateOption(option: CliOption) extends CliError:

    override val message: String =
      s"Option specified more than once: ${option.flag}"

  final case class UnexpectedArgument(argument: String) extends CliError:

    override val message: String =
      s"""Unexpected argument: $argument
         |${CliUsage.text}""".stripMargin

  final case class InvalidFormat(
    provided: String,
    supported: List[String]) extends CliError:

    override val message: String =
      s"Unsupported format: $provided. Supported formats: ${supported.mkString(", ")}"

  final case class RuntimeInitialization(details: String) extends CliError:

    override val message: String =
      details

  final case class RenderFailure(details: String) extends CliError:

    override val message: String =
      details
