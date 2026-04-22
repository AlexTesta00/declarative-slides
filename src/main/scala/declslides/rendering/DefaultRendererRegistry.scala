package declslides.rendering

import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer

/** Production renderer registry shipped with DeclSlides.
  *
  * This registry exposes the renderers available out of the box.
  */
object DefaultRendererRegistry:

  /** Default live registry used by the CLI and the bootstrap script. */
  val live: RendererRegistry =
    RendererRegistry(
      TextRenderer,
      HtmlRenderer,
    )
