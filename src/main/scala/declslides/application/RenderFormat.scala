package declslides.application

enum RenderFormat derives CanEqual:
  case Html
  case Text

  def fileExtension: String = this match
    case Html => "html"
    case Text => "txt"

object RenderFormat:

  def parse(raw: String): Either[ApplicationError, RenderFormat] =
    raw.toLowerCase match
      case "html" => Right(RenderFormat.Html)
      case "text" | "txt" => Right(RenderFormat.Text)
      case other =>
        Left(
          ApplicationError.InvalidCommand(
            s"Unsupported format '$other'. Expected one of: html, text",
          ),
        )
