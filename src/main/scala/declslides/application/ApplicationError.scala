package declslides.application

/** Errors raised while turning a valid CLI request into a rendered document.
  *
  * These errors belong to the application layer: they describe invalid inputs,
  * unsupported formats, runtime execution failures, and other problems that can
  * happen while rendering a presentation.
  */
enum ApplicationError derives CanEqual:
  /** The input file does not exist. */
  case InputFileNotFound(path: String)

  /** The input file exists, but its kind is not supported. */
  case UnsupportedInputFile(path: String)

  /** The input file exists but could not be read. */
  case CannotReadInput(
    path: String,
    reason: String)

  /** The requested output format is not available. */
  case UnsupportedFormat(
    raw: String,
    supported: Vector[String])

  /** Scala CLI could not be executed. */
  case ScalaCliUnavailable(
    binary: String,
    reason: String)

  /** The generated bootstrap script failed during execution. */
  case ScriptExecutionFailed(details: String)

  /** Returns description of the error. */
  def message: String = this match
    case InputFileNotFound(path) =>
      s"Input file not found: $path"

    case UnsupportedInputFile(path) =>
      s"Unsupported input file: $path. Expected a .sc script containing a DeclSlides expression"

    case CannotReadInput(path, reason) =>
      s"Cannot read input file '$path': $reason"

    case UnsupportedFormat(raw, supported) =>
      s"Unsupported format '$raw'. Expected one of: ${supported.mkString(", ")}"

    case ScalaCliUnavailable(binary, reason) =>
      s"Cannot execute '$binary': $reason"

    case ScriptExecutionFailed(details) =>
      s"Script execution failed:\n$details"
