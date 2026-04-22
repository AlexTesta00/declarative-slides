package declslides.rendering.html

import java.nio.charset.StandardCharsets

object ResourceTextLoader:

  def load(path: String): Either[String, String] =
    Option(getClass.getResourceAsStream(path))
      .toRight(s"Resource not found: $path")
      .flatMap { stream =>
        try
          Right(
            new String(
              stream.readAllBytes(),
              StandardCharsets.UTF_8,
            ),
          )
        catch
          case error: Exception =>
            Left(
              s"Cannot read resource '$path': ${safeMessage(error)}",
            )
        finally
          stream.close()
      }

  private def safeMessage(error: Throwable): String =
    Option(error.getMessage)
      .map(_.trim)
      .filter(_.nonEmpty)
      .getOrElse(error.getClass.getSimpleName)
