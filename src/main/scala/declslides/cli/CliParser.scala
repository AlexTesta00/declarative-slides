package declslides.cli

import declslides.application.ApplicationError
import declslides.application.RenderFormat

object CliParser:

  def parse(args: List[String]): Either[ApplicationError, CliCommand] =
    args match
      case Nil =>
        Right(CliCommand.Help)

      case "help" :: Nil =>
        Right(CliCommand.Help)

      case "list" :: Nil =>
        Right(CliCommand.ListPresentations)

      case "render" :: name :: format :: Nil =>
        RenderFormat.parse(format).map(parsed =>
          CliCommand.Render(name, parsed),
        )

      case _ =>
        Left(
          ApplicationError.InvalidCommand(
            "Usage: list | help | render <presentation-name> <html|text>",
          ),
        )
