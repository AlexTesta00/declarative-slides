package declslides.cli

import declslides.application.RenderCommand
import declslides.application.ScalaCliScriptRunner
import declslides.rendering.DefaultRendererRegistry

object DeclSlidesCli:

  private def defaultCommand: Either[String, RenderCommand] =
    DeclSlidesRuntime.scalaCliBinary.map { scalaCliBinary =>
      RenderCommand(
        registry = DefaultRendererRegistry.live,
        runner = ScalaCliScriptRunner(
          declslidesDependency = DeclSlidesRuntime.coreDependency,
          scalaCliBinary = scalaCliBinary,
          scalaVersion = DeclSlidesRuntime.scalaVersion,
        ),
      )
    }

  private[declslides] def run(
    args: Array[String],
    printError: String => Unit = System.err.println,
  ): Int =
    defaultCommand match
      case Left(error) =>
        printError(s"[error] $error")
        1

      case Right(command) =>
        args.toList match
          case "--input" :: input :: "--format" :: format :: "--output" ::
              output :: Nil =>
            command
              .run(
                input = os.Path(input, os.pwd),
                format = format,
                output = os.Path(output, os.pwd),
              )
              .fold(
                error =>
                  printError(s"[error] ${error.message}")
                  1
                ,
                _ => 0,
              )

          case _ =>
            printError(
              """Usage:
                |  declslides --input <input.sc> --format <html|text|txt> --output <output>
                |""".stripMargin.trim,
            )
            1

  def main(args: Array[String]): Unit =
    val exitCode =
      run(args)

    if exitCode != 0 then
      sys.exit(exitCode)
