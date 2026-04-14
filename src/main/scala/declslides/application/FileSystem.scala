package declslides.application

trait FileSystem:

  def write(
    path: String,
    content: String,
  ): Either[ApplicationError, Unit]
