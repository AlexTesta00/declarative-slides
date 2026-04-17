package declslides

import declslides.application.InMemoryPresentationRegistry
import declslides.application.PresentationRegistry
import declslides.application.RenderPresentation
import declslides.cli.CliExitCode
import declslides.cli.CliHandler
import declslides.cli.CliMessages
import declslides.cli.CliParser
import declslides.cli.JvmFileSystem
import declslides.cli.StdOutput
import declslides.rendering.RendererRegistry
import declslides.rendering.html.HtmlRenderer
import declslides.rendering.text.TextRenderer

object Main:

  private final case class CliRuntime(
    rendererRegistry: RendererRegistry,
    parser: CliParser,
    handler: CliHandler)

  private def buildRendererRegistry(): RendererRegistry =
    RendererRegistry(
      new HtmlRenderer,
      new TextRenderer,
    )

  private def buildPresentationRegistry(): PresentationRegistry =
    InMemoryPresentationRegistry()

  private def buildRenderPresentation(
    presentationRegistry: PresentationRegistry,
    rendererRegistry: RendererRegistry,
  ): RenderPresentation =
    new RenderPresentation(
      registry = presentationRegistry,
      rendererRegistry = rendererRegistry,
      fileSystem = new JvmFileSystem,
    )

  private def buildRuntime(): CliRuntime =
    val rendererRegistry = buildRendererRegistry()
    val presentationRegistry = buildPresentationRegistry()
    val renderPresentation =
      buildRenderPresentation(presentationRegistry, rendererRegistry)

    CliRuntime(
      rendererRegistry = rendererRegistry,
      parser = new CliParser(rendererRegistry),
      handler = new CliHandler(
        presentationRegistry = presentationRegistry,
        renderPresentation = renderPresentation,
        rendererRegistry = rendererRegistry,
        output = StdOutput,
      ),
    )

  private def handleParseError(
    errorMessage: String,
    rendererRegistry: RendererRegistry,
  ): CliExitCode =
    StdOutput.writeLine(CliMessages.renderErrorMessage(errorMessage))
    StdOutput.writeLine(CliMessages.helpText(rendererRegistry))
    CliExitCode.Failure

  private def run(args: List[String]): CliExitCode =
    val runtime = buildRuntime()

    runtime.parser
      .parse(args)
      .fold(
        error => handleParseError(error.message, runtime.rendererRegistry),
        command => runtime.handler.handle(command),
      )

  @main def runCli(args: String*): Unit =
    val exitCode = run(args.toList)

    if exitCode != CliExitCode.Success then
      sys.exit(exitCode.code)
