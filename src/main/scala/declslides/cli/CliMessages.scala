package declslides.cli

import declslides.rendering.RenderFormat
import declslides.rendering.RendererRegistry

object CliMessages:

  val availablePresentationsHeader: String =
    "Available presentations:"

  val presentationBulletPrefix: String =
    "- "

  def helpText(rendererRegistry: RendererRegistry): String =
    s"""Declarative-Slides CLI
       |
       |Commands:
       |  help
       |  list
       |  render <presentation-name> <${rendererRegistry.supportedLabels.mkString(
        "|",
      )}>
       |""".stripMargin

  def renderSuccessMessage(
    presentationName: String,
    format: RenderFormat,
  ): String =
    s"Rendered '$presentationName' as ${format.label}."

  def renderErrorMessage(message: String): String =
    s"Error: $message"
