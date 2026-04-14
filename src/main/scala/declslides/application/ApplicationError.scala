package declslides.application

enum ApplicationError derives CanEqual:
  case PresentationNotFound(name: String)

  case WriteFailure(
    path: String,
    reason: String)

  case InvalidCommand(reason: String)

  def message: String = this match
    case PresentationNotFound(name) =>
      s"Presentation '$name' was not found"
    case WriteFailure(path, reason) =>
      s"Unable to write '$path': $reason"
    case InvalidCommand(reason) =>
      reason
