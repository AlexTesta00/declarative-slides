package declslides.application

/** Produces the bootstrap Scala source executed by the script runner.
  *
  * The generated source wraps the user script, resolves the presentation, picks
  * the requested renderer, and writes the final document to disk.
  */
trait BootstrapSourceFactory:

  /** Creates the bootstrap source for a user script. */
  def create(
    userSource: String,
    declslidesDependency: String,
    scalaVersion: Option[String],
  ): String

/** Default bootstrap source factory for the Scala CLI runner. */
object ScalaCliBootstrapSourceFactory extends BootstrapSourceFactory:

  private val UserSourceIndent = 6

  override def create(
    userSource: String,
    declslidesDependency: String,
    scalaVersion: Option[String],
  ): String =
    val scalaDirective =
      scalaVersion.fold("")(version => s"//> using scala $version\n")

    s"""|$scalaDirective//> using dep $declslidesDependency
        |
        |import declslides.dsl.DSL.*
        |import declslides.domain.*
        |import declslides.rendering.DefaultRendererRegistry
        |
        |import java.nio.charset.StandardCharsets
        |import java.nio.file.{Files, Path}
        |
        |object Bootstrap:
        |  def resolve(
        |    result: Either[Vector[DomainError], Presentation],
        |  ): Presentation =
        |    result match
        |      case Right(presentation) =>
        |        presentation
        |
        |      case Left(errors) =>
        |        val renderedErrors =
        |          errors.map(error => s"- $${error.message}").mkString("\\n")
        |
        |        System.err.println(
        |          "Presentation validation failed:\\n" + renderedErrors,
        |        )
        |
        |        sys.exit(1)
        |
        |@main def render(format: String, output: String): Unit =
        |  val resolvedPresentation =
        |    Bootstrap.resolve {
        |${indent(userSource, UserSourceIndent)}
        |    }
        |
        |  val registry =
        |    DefaultRendererRegistry.live
        |
        |  val target =
        |    registry.parse(format).getOrElse {
        |      System.err.println(
        |        "Unsupported format '" + format + "'. Supported formats: " +
        |          registry.supportedLabels.mkString(", "),
        |      )
        |      sys.exit(1)
        |    }
        |
        |  val renderer =
        |    registry.resolve(target).getOrElse {
        |      System.err.println(
        |        "No renderer registered for format '" + target.label + "'",
        |      )
        |      sys.exit(1)
        |    }
        |
        |  val destination =
        |    Path.of(output)
        |
        |  val parent =
        |    destination.getParent
        |
        |  if parent != null then
        |    Files.createDirectories(parent)
        |
        |  Files.writeString(
        |    destination,
        |    renderer.render(resolvedPresentation).content,
        |    StandardCharsets.UTF_8,
        |  )
        |""".stripMargin

  private def indent(
    value: String,
    spaces: Int,
  ): String =
    val padding =
      " " * spaces

    value.linesIterator
      .map(line => s"$padding$line")
      .mkString("\n")
