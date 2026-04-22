package declslides.cli

/** Errors raised by the CLI layer.
  *
  * These errors describe problems that are meaningful from the user's point of
  * view: invalid arguments, unsupported options, initialization failures, or
  * rendering failures surfaced through the command line.
  */
sealed trait CliError:
  /** Human-readable message shown to the user. */
  def message: String

/** Constructors for the concrete CLI error cases. */
object CliError:

  /** A required option was not provided. */
  final case class MissingOption(option: CliOption) extends CliError:

    override val message: String =
      s"""Missing required option: ${option.flag}
         |${CliUsage.text}""".stripMargin

  /** An option flag was provided without a value. */
  final case class MissingValue(option: String) extends CliError:

    override val message: String =
      s"""Missing value for option: $option
         |${CliUsage.text}""".stripMargin

  /** An unknown option flag was provided. */
  final case class UnknownOption(option: String) extends CliError:

    override val message: String =
      s"""Unknown option: $option
         |${CliUsage.text}""".stripMargin

  /** The same option was provided more than once. */
  final case class DuplicateOption(option: CliOption) extends CliError:

    override val message: String =
      s"Option specified more than once: ${option.flag}"

  /** A positional or otherwise unexpected argument was encountered. */
  final case class UnexpectedArgument(argument: String) extends CliError:

    override val message: String =
      s"""Unexpected argument: $argument
         |${CliUsage.text}""".stripMargin

  /** The user requested an unsupported output format. */
  final case class InvalidFormat(
    provided: String,
    supported: List[String]) extends CliError:

    override val message: String =
      s"Unsupported format: $provided. Supported formats: ${supported.mkString(", ")}"

  /** The runtime environment could not be prepared. */
  final case class RuntimeInitialization(details: String) extends CliError:

    override val message: String =
      details

  /** The rendering command failed after the CLI was parsed successfully. */
  final case class RenderFailure(details: String) extends CliError:

    override val message: String =
      details
