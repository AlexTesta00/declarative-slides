package declslides.application

type Error = ApplicationError

trait FileSystem:

  def write(
    path: String,
    content: String,
  ): Either[Error, Unit]

object FileSystem:

  val noop: FileSystem = (
    path: String,
    content: String,
  ) => Right(())
