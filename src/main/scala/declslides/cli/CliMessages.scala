package declslides.cli

import declslides.rendering.RenderingTarget

object CliMessages:

  private val commandNameHelp = "help"
  private val commandNameList = "list"
  private val commandNameRender = "render"

  val availablePresentationsHeader: String =
    "Available presentations:"

  val presentationBulletPrefix: String =
    "- "

  def renderSuccessMessage(
    presentationName: String,
    format: RenderingTarget,
  ): String =
    s"Rendered '$presentationName' as ${format.label}."

  def renderErrorMessage(message: String): String =
    s"Error: $message"

  val helpText: String =
    s"""Declarative-Slides CLI
       |
       |Commands:
       |  $commandNameHelp
       |  $commandNameList
       |  $commandNameRender <presentation-name> <${RenderingTarget.supportedLabels.mkString(
        "|",
      )}>
       |""".stripMargin
