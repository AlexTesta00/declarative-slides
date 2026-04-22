package declslides.application

/** Normalizes throwable messages for user-facing error reporting.
  *
  * This object turns exceptions into readable messages without leaking `null`
  * or blank strings into the rest of the application.
  */
object ErrorMessage:

  /** Returns a trimmed message for the given throwable.
    *
    * If the throwable has no useful message, this method falls back to the
    * simple name of the exception class.
    */
  def apply(error: Throwable): String =
    Option(error.getMessage)
      .map(_.trim)
      .filter(_.nonEmpty)
      .getOrElse(error.getClass.getSimpleName)
