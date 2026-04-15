package declslides.cli

import declslides.application.ApplicationError
import declslides.rendering.RenderingTarget

object CliParser:

  private val usageMessage =
    s"Usage: list | help | render <presentation-name> <${RenderingTarget.supportedLabels.mkString("|")}>"

  def parse(args: List[String]): Either[ApplicationError, CliCommand] =
    args match
      case Nil =>
        Right(CliCommand.Help)

      case "help" :: Nil =>
        Right(CliCommand.Help)

      case "list" :: Nil =>
        Right(CliCommand.ListPresentations)

      case "render" :: name :: format :: Nil =>
        RenderingTarget.parse(format).map(CliCommand.Render(name, _))

      case _ =>
        Left(ApplicationError.InvalidCommand(usageMessage))
