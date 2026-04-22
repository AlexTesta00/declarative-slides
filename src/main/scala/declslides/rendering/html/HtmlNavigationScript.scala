package declslides.rendering.html

object HtmlNavigationScript:

  private val ResourcePath =
    "/declslides/rendering/html/navigation.js"

  lazy val content: Either[String, String] =
    ResourceTextLoader.load(ResourcePath)
