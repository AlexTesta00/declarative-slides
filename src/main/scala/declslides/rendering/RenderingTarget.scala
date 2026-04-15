package declslides.rendering

import declslides.application.ApplicationError

enum RenderingTarget derives CanEqual:
  case Text
  case Html

  def fileExtension: FileExtension =
    this match
      case Text => "txt"
      case Html => "html"

  def label: String =
    this match
      case Text => "text"
      case Html => "html"

  private def acceptedInputFormats: Set[FileExtension] =
    this match
      case Text => Set("txt", "text")
      case Html => Set("html")

object RenderingTarget:

  def parse(raw: String): Either[ApplicationError, RenderingTarget] =
    RenderingTarget.values
      .find(_.acceptedInputFormats.contains(raw.toLowerCase))
      .toRight(
        ApplicationError.InvalidCommand(
          s"Unsupported format '$raw'. Expected one of: ${supportedLabels.mkString(", ")}",
        ),
      )

  def supportedLabels: Vector[String] =
    RenderingTarget.values.map(_.label).toVector
