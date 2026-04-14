package declslides.application

type Error = ApplicationError
type Nothing = Unit

trait FileSystem:

  def write(
    path: String,
    content: String,
  ): Either[Error, Nothing]

object FileSystem:

  val noop: FileSystem = (
    path: String,
    content: String,
  ) => Right(())
