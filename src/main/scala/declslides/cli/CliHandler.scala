package declslides.cli

import declslides.application.PresentationRegistry
import declslides.application.RenderPresentation
import declslides.application.RenderRequest
import declslides.rendering.RenderingTarget

final class CliHandler(
  registry: PresentationRegistry,
  renderPresentation: RenderPresentation,
  output: OutputPort):

  def handle(command: CliCommand): Int =
    command match
      case CliCommand.Help =>
        showHelp()

      case CliCommand.ListPresentations =>
        showAvailablePresentations()

      case CliCommand.Render(name, format) =>
        renderPresentationToOutput(name, format)

  private def showHelp(): Int =
    output.writeLine(CliMessages.helpText)
    CliExitCode.Success

  private def showAvailablePresentations(): Int =
    output.writeLine(CliMessages.availablePresentationsHeader)
    registry.available.foreach { name =>
      output.writeLine(s"${CliMessages.presentationBulletPrefix}$name")
    }
    CliExitCode.Success

  private def renderPresentationToOutput(
    name: String,
    format: RenderingTarget,
  ): Int =
    renderPresentation.run(RenderRequest(name, format, None)) match
      case Right(result) =>
        output.writeLine(CliMessages.renderSuccessMessage(name, format))
        output.writeLine(result.document.content)
        CliExitCode.Success

      case Left(error) =>
        output.writeLine(CliMessages.renderErrorMessage(error.message))
        CliExitCode.Failure
