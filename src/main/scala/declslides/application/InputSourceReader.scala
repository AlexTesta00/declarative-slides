package declslides.application

import scala.util.control.NonFatal

/** Reads the source code of an input presentation script.
  *
  * Keeping file reading behind an abstraction makes the runner easier to test
  * and keeps I/O details out of the orchestration flow.
  */
trait InputSourceReader:

  /** Reads the script source for the given input path. */
  def read(input: os.Path): Either[ApplicationError, String]

/** File-system based [[InputSourceReader]] backed by `os-lib`. */
object OsInputSourceReader extends InputSourceReader:

  override def read(
    input: os.Path,
  ): Either[ApplicationError, String] =
    try
      Right(os.read(input))
    catch
      case NonFatal(error) =>
        Left(
          ApplicationError.CannotReadInput(
            path = input.toString,
            reason = ErrorMessage(error),
          ),
        )
