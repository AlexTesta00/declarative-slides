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
    registry.resolve(request.presentationName) match
      case Left(error) =>
        Left(error)

      case Right(presentation) =>
        val renderer =
          request.format match
            case RenderFormat.Html => htmlRenderer
            case RenderFormat.Text => textRenderer

        val document =
          renderer.render(presentation)

        request.outputPath match
          case None =>
            Right(RenderResult(document, None))

          case Some(path) =>
            fileSystem.write(path, document.content) match
              case Left(error) => Left(error)
              case Right(_) => Right(RenderResult(document, Some(path)))
