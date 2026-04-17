package declslides.cli

import declslides.application.PresentationRegistry
import declslides.application.RenderPresentation
import declslides.application.RenderRequest
import declslides.rendering.RenderFormat
import declslides.rendering.RendererRegistry

final class CliHandler(
  presentationRegistry: PresentationRegistry,
  renderPresentation: RenderPresentation,
  rendererRegistry: RendererRegistry,
  output: OutputPort):

  def handle(command: CliCommand): CliExitCode =
    command match
      case CliCommand.Help =>
        showHelp()

      case CliCommand.ListPresentations =>
        showAvailablePresentations()

      case CliCommand.Render(name, format, outputPath) =>
        renderPresentationToOutput(name, format, outputPath)

  private def showHelp(): CliExitCode =
    output.writeLine(CliMessages.helpText(rendererRegistry))
    CliExitCode.Success

  private def showAvailablePresentations(): CliExitCode =
    output.writeLine(CliMessages.availablePresentationsHeader)
    presentationRegistry.available.foreach { name =>
      output.writeLine(s"${CliMessages.presentationBulletPrefix}$name")
    }
    CliExitCode.Success

  private def renderPresentationToOutput(
    name: String,
    format: RenderFormat,
    outputPath: Option[String],
  ): CliExitCode =
    renderPresentation.run(RenderRequest(name, format, outputPath)) match
      case Right(result) =>
        output.writeLine(CliMessages.renderSuccessMessage(name, format))
        outputPath match
          case Some(path) =>
            output.writeLine(CliMessages.renderWrittenMessage(path))
          case None =>
            output.writeLine(result.document.content)
        CliExitCode.Success

      case Left(error) =>
        output.writeLine(CliMessages.renderErrorMessage(error.message))
        CliExitCode.Failure
