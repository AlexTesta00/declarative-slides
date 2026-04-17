package declslides.cli

import declslides.application.ApplicationError
import declslides.rendering.RendererRegistry

final class CliParser(
  rendererRegistry: RendererRegistry):

  private def usageMessage: String =
    s"Usage: list | help | render <presentation-name> <${rendererRegistry.supportedLabels.mkString("|")}> [output-path]"

  private def unsupportedFormatMessage(raw: String): String =
    s"Unsupported format '$raw'. Expected one of: ${rendererRegistry.supportedLabels.mkString(", ")}"

  def parse(args: List[String]): Either[ApplicationError, CliCommand] =
    args match
      case Nil =>
        Right(CliCommand.Help)

      case "help" :: Nil =>
        Right(CliCommand.Help)

      case "list" :: Nil =>
        Right(CliCommand.ListPresentations)

      case "render" :: name :: rawFormat :: Nil =>
        rendererRegistry
          .parse(rawFormat)
          .toRight(
            ApplicationError.InvalidCommand(
              unsupportedFormatMessage(rawFormat),
            ),
          )
          .map(CliCommand.Render(name, _, None))

      case "render" :: name :: rawFormat :: outputPath :: Nil =>
        rendererRegistry
          .parse(rawFormat)
          .toRight(
            ApplicationError.InvalidCommand(
              unsupportedFormatMessage(rawFormat),
            ),
          )
          .map(CliCommand.Render(name, _, Some(outputPath)))

      case _ =>
        Left(
          ApplicationError.InvalidCommand(
            usageMessage,
          ),
        )
