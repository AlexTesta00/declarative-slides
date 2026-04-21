package declslides.cli

object CliUsage:

  val text: String =
    s"""Usage:
       |  declslides --input <input.sc> --format <${OutputFormat.supportedValues.mkString(
        "|",
      )}> --output <output>
       |""".stripMargin.trim
