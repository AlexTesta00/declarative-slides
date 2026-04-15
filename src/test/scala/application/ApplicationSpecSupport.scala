package application

import declslides.application.ApplicationError
import declslides.application.FileSystem
import declslides.application.InMemoryPresentationRegistry
import declslides.application.RenderPresentation
import declslides.domain.Presentation
import declslides.dsl.DSL._
import declslides.rendering.RendererRegistry
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.matchers.should.Matchers

trait ApplicationSpecSupport extends Matchers:

  protected val sampleDeck: Presentation =
    presentation("Demo"):
      deck(
        slide("Intro"):
          content(
            text("Hello"),
          ),
      )
    .value

  protected final class RecordingFileSystem extends FileSystem:
    var writes: Map[String, String] = Map.empty

    override def write(
      path: String,
      content: String,
    ): Either[ApplicationError, Unit] =
      writes = writes.updated(path, content)
      Right(())

  protected def registry(entries: (String, Presentation)*) =
    InMemoryPresentationRegistry(entries*)

  protected def rendererRegistry(): RendererRegistry =
    RendererRegistry(
      new HtmlRenderer,
      new TextRenderer,
    )

  protected def service(
    entries: (String, Presentation)*,
  ): (RenderPresentation, RecordingFileSystem) =
    val fs = new RecordingFileSystem
    val renderPresentation =
      new RenderPresentation(
        registry = registry(entries*),
        rendererRegistry = rendererRegistry(),
        fileSystem = fs,
      )

    (renderPresentation, fs)
