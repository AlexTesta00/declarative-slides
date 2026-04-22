package declslides.rendering.html

/** Access point for the HTML viewer navigation script. */
object HtmlNavigationScript:

  private val ResourcePath =
    "/declslides/rendering/html/navigation.js"

  /** Return the navigation script content. */
  lazy val content: Either[String, String] =
    ResourceTextLoader.load(ResourcePath)
