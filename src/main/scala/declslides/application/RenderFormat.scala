package declslides.application

enum RenderFormat:
  case Html
  case Text

  def fileExtension: String =
    this match
      case Html => "html"
      case Text => "txt"

object RenderFormat:

  def parse(raw: String): Either[ApplicationError, RenderFormat] =
    raw.toLowerCase match
      case "html" =>
        Right(RenderFormat.Html)
      case "text" =>
        Right(RenderFormat.Text)
      case "txt" =>
        Right(RenderFormat.Text)
      case other =>
        Left(
          ApplicationError.InvalidCommand(
            s"Unsupported format '$other'. Expected one of: html, text",
          ),
        )
