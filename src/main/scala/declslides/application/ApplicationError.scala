package declslides.application

enum ApplicationError derives CanEqual:
  case InputFileNotFound(path: String)
  case UnsupportedInputFile(path: String)

  case CannotReadInput(
    path: String,
    reason: String)

  case UnsupportedFormat(
    raw: String,
    supported: Vector[String])

  case ScalaCliUnavailable(
    binary: String,
    reason: String)

  case ScriptExecutionFailed(details: String)

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
