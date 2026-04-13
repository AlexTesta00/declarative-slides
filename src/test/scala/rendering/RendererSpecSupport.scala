package rendering

import declslides.domain.Layout
import declslides.domain.Presentation
import declslides.dsl.DSL._
import declslides.rendering.Document
import declslides.rendering.Renderer
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.matchers.should.Matchers

trait RendererSpecSupport extends Matchers:

  protected def renderer: Renderer

  protected def demoDeck(items: PresBuild*): Presentation =
    presentation("Demo"):
      deck(items*)
    .value

  protected def render(items: PresBuild*): Document =
    renderer.render(demoDeck(items*))

  protected def renderedContent(items: PresBuild*): String =
    render(items*).content

  protected def singleSlideContent(title: String = "Intro")(items: SlideBuild*)
    : String =
    renderedContent(
      slide(title):
        content(items*),
    )

  protected def singleSlideContentWithLayout(
    title: String,
    layout: Layout,
  )(items: SlideBuild*,
  ): String =
    renderedContent(
      slide(title, layout):
        content(items*),
    )
