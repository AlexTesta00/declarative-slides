package declslides.application

trait InputScriptValidator:
  def validate(input: os.Path): Either[ApplicationError, Unit]

object DefaultInputScriptValidator extends InputScriptValidator:

  private val SupportedExtension = "sc"

  override def validate(
    input: os.Path,
  ): Either[ApplicationError, Unit] =
    if !os.exists(input) then
      Left(ApplicationError.InputFileNotFound(input.toString))
    else if input.ext != SupportedExtension then
      Left(ApplicationError.UnsupportedInputFile(input.toString))
    else
      Right(())
