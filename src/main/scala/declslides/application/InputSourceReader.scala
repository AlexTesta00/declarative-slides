package declslides.application

import scala.util.control.NonFatal

trait InputSourceReader:
  def read(input: os.Path): Either[ApplicationError, String]

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
