package declslides.application

/** Validates that an input path is usable as a DeclSlides script.
  *
  * Validation is kept separate from script execution so that the rules stay
  * explicit and easy to evolve.
  */
trait InputScriptValidator:

  /** Validates the input script path. */
  def validate(input: os.Path): Either[ApplicationError, Unit]

/** Default validator for DeclSlides input scripts.
  *
  * The default policy is intentionally simple: the file must exist and must use
  * the `.sc` extension.
  */
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
