package declslides.cli

/** User-facing usage text for the `declslides` command.
  *
  * Keeping the usage string in one place avoids drift between parsing logic and
  * error reporting.
  */
object CliUsage:

  /** Canonical usage text shown when the invocation is invalid. */
  val text: String =
    s"""Usage:
       |  declslides --input <input.sc> --format <${OutputFormat.supportedValues.mkString(
        "|",
      )}> --output <output>
       |""".stripMargin.trim
