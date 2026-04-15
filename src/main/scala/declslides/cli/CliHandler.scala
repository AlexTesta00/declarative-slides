package declslides.cli

import declslides.application.PresentationRegistry
import declslides.application.RenderPresentation
import declslides.application.RenderRequest

trait OutputPort:
  def writeLine(line: String): Unit

object StdOutput extends OutputPort:

  override def writeLine(line: String): Unit =
    println(line)

final class CliHandler(
  registry: PresentationRegistry,
  renderPresentation: RenderPresentation,
  output: OutputPort):

  def handle(command: CliCommand): Int =
    command match
      case CliCommand.Help =>
        output.writeLine(CliHandler.helpText)
        0

      case CliCommand.ListPresentations =>
        output.writeLine("Available presentations:")
        registry.available.foreach(name => output.writeLine(s"- $name"))
        0

      case CliCommand.Render(name, format) =>
        renderPresentation.run(RenderRequest(name, format, None)) match
          case Right(result) =>
            output.writeLine(
              s"Rendered '$name' as ${format.toString.toLowerCase}.",
            )
            output.writeLine(result.document.content)
            0

          case Left(error) =>
            output.writeLine(s"Error: ${error.message}")
            1

object CliHandler:

  val helpText: String =
    """Declarative-Slides CLI
      |
      |Commands:
      |  help
      |  list
      |  render <presentation-name> <html|text>
      |""".stripMargin
