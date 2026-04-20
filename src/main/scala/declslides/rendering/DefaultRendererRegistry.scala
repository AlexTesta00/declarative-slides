package declslides.rendering

import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer

object DefaultRendererRegistry:

  val live: RendererRegistry =
    RendererRegistry(
      TextRenderer,
      HtmlRenderer,
    )
