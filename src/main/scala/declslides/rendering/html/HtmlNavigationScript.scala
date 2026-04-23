package declslides.rendering.html

import declslides.utils.ResourceTextLoader

type ResourceError = String

/** Access point for the HTML viewer navigation script. */
object HtmlNavigationScript:

  private val ResourcePath =
    "/declslides/rendering/html/navigation.js"

  /** Returns the navigation script content. */
  lazy val content: Either[ResourceError, String] =
    ResourceTextLoader.load(ResourcePath)
