package declslides.application

import declslides.rendering.Document
import declslides.rendering.Renderer
import declslides.rendering.RenderingTarget
import declslides.rendering.RenderingTarget.Html
import declslides.rendering.RenderingTarget.Text

final case class RenderRequest(
  presentationName: String,
  format: RenderingTarget,
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

  private def rendererFor(format: RenderingTarget): Renderer =
    format match
      case Html => htmlRenderer
      case Text => textRenderer
