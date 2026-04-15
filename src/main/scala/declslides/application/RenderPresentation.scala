package declslides.application

import declslides.rendering.Document
import declslides.rendering.RenderFormat
import declslides.rendering.Renderer
import declslides.rendering.RendererRegistry

final case class RenderRequest(
  presentationName: String,
  format: RenderFormat,
  outputPath: Option[String])

final case class RenderResult(
  document: Document,
  writtenTo: Option[String])

final class RenderPresentation(
  registry: PresentationRegistry,
  rendererRegistry: RendererRegistry,
  fileSystem: FileSystem):

  def run(request: RenderRequest): Either[ApplicationError, RenderResult] =
    for
      presentation <- registry.resolve(request.presentationName)
      renderer <- rendererFor(request.format)
      document = renderer.render(presentation)
      _ <- request.outputPath match
        case Some(path) => fileSystem.write(path, document.content)
        case None => Right(())
    yield RenderResult(document, request.outputPath)

  private def rendererFor(format: RenderFormat)
    : Either[ApplicationError, Renderer] =
    rendererRegistry
      .resolve(format)
      .toRight(
        ApplicationError.InvalidCommand(
          s"No renderer available for format '${format.label}'",
        ),
      )
