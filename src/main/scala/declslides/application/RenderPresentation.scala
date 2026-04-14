package declslides.application

import declslides.rendering.Document
import declslides.rendering.Renderer

final case class RenderRequest(
  presentationName: String,
  format: RenderFormat,
  outputPath: Option[String])

final case class RenderResult(
  document: Document,
  writtenTo: Option[String])

final class RenderPresentation(
  registry: PresentationRegistry,
  htmlRenderer: Renderer,
  textRenderer: Renderer,
  fileSystem: FileSystem):

  def run(request: RenderRequest): Either[ApplicationError, RenderResult] =
    for
      presentation <- registry.resolve(request.presentationName)
      renderer = rendererFor(request.format)
      document = renderer.render(presentation)
      _ <- request.outputPath match
        case Some(path) => fileSystem.write(path, document.content)
        case None => Right(())
    yield RenderResult(document, request.outputPath)

  private def rendererFor(format: RenderFormat): Renderer =
    format match
      case RenderFormat.Html => htmlRenderer
      case RenderFormat.Text => textRenderer
