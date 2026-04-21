package declslides.cli

object CliArgumentParser:

  def parse(
    args: Array[String],
    workingDirectory: os.Path = os.pwd,
  ): Either[CliError, CliConfig] =
    for
      options <- parseOptions(args.toList)
      inputValue <- required(options, CliOption.Input)
      formatValue <- required(options, CliOption.Format)
      outputValue <- required(options, CliOption.Output)
      format <- OutputFormat.parse(formatValue)
    yield CliConfig(
      input = os.Path(inputValue, workingDirectory),
      format = format,
      output = os.Path(outputValue, workingDirectory),
    )

  private def required(
    options: Map[CliOption, String],
    option: CliOption,
  ): Either[CliError, String] =
    options.get(option).toRight(CliError.MissingOption(option))

  private def parseOptions(
    args: List[String],
  ): Either[CliError, Map[CliOption, String]] =
    parseOptionsRec(args, Map.empty)

  private def parseOptionsRec(
    remaining: List[String],
    collected: Map[CliOption, String],
  ): Either[CliError, Map[CliOption, String]] =
    remaining match
      case Nil =>
        Right(collected)

      case flag :: Nil =>
        Left(CliError.MissingValue(flag))

      case rawFlag :: value :: tail =>
        for
          option <- parseOption(rawFlag)
          _ <- ensureNotDuplicated(option, collected)
          result <- parseOptionsRec(
            remaining = tail,
            collected = collected.updated(option, value),
          )
        yield result

  private def parseOption(rawFlag: String): Either[CliError, CliOption] =
    if rawFlag.startsWith("--") then
      CliOption.parse(rawFlag)
    else
      Left(CliError.UnexpectedArgument(rawFlag))

  private def ensureNotDuplicated(
    option: CliOption,
    collected: Map[CliOption, String],
  ): Either[CliError, Unit] =
    Either.cond(
      !collected.contains(option),
      (),
      CliError.DuplicateOption(option),
    )
