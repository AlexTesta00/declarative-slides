package declslides.rendering.html

import java.nio.charset.StandardCharsets
import scala.util.Using

/** Utility for loading UTF-8 text resources from the classpath. */
object ResourceTextLoader:

  /** Loads a text resource from the classpath.
    *
    * The loader supports both classloader-style and class-style lookups so it
    * behaves consistently across local runs, tests, and CI.
    *
    * @param path
    *   classpath resource path, with or without a leading slash
    * @return
    *   the resource content, or a readable loading error
    */
  def load(path: String): Either[String, String] =
    val absolutePath =
      if path.startsWith("/") then path else s"/$path"

    val normalizedPath =
      absolutePath.stripPrefix("/")

    val stream =
      Option(Thread.currentThread().getContextClassLoader)
        .flatMap(loader => Option(loader.getResourceAsStream(normalizedPath)))
        .orElse(Option(
          getClass.getClassLoader.getResourceAsStream(normalizedPath),
        ))
        .orElse(Option(getClass.getResourceAsStream(absolutePath)))

    stream
      .toRight(s"Resource not found: $absolutePath")
      .flatMap { input =>
        Using(input) { in =>
          new String(
            in.readAllBytes(),
            StandardCharsets.UTF_8,
          )
        }.toEither.left.map { error =>
          s"Cannot read resource '$absolutePath': ${safeMessage(error)}"
        }
      }

  private def safeMessage(error: Throwable): String =
    Option(error.getMessage)
      .map(_.trim)
      .filter(_.nonEmpty)
      .getOrElse(error.getClass.getSimpleName)
