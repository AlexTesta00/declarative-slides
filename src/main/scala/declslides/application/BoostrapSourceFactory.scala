package declslides.application

import declslides.utils.ResourceTextLoader

/** Produces the bootstrap Scala source executed by the script runner.
  *
  * The generated source wraps the user script, resolves the presentation, picks
  * the requested renderer, and writes the final document to disk.
  */
trait BootstrapSourceFactory:

  /** Creates the bootstrap source for a user script.
    *
    * @return
    *   the generated bootstrap source, or an application error if the template
    *   cannot be loaded
    */
  def create(
    userSource: String,
    declslidesDependency: String,
    scalaVersion: Option[String],
  ): Either[ApplicationError, String]

/** Default bootstrap source factory for the Scala CLI runner. */
object ScalaCliBootstrapSourceFactory extends BootstrapSourceFactory:

  private val TemplatePath =
    "/declslides/application/bootstrap-template.txt"

  private val UserSourceIndent = 6

  private val ScalaDirectivePlaceholder =
    "{{SCALA_DIRECTIVE}}"

  private val DependencyPlaceholder =
    "{{DECLSLIDES_DEPENDENCY}}"

  private val UserSourcePlaceholder =
    "{{USER_SOURCE}}"

  override def create(
    userSource: String,
    declslidesDependency: String,
    scalaVersion: Option[String],
  ): Either[ApplicationError, String] =
    ResourceTextLoader
      .load(TemplatePath)
      .left
      .map { reason =>
        ApplicationError.BootstrapTemplateUnavailable(
          path = TemplatePath,
          reason = reason,
        )
      }
      .map(template =>
        fillTemplate(
          template = template,
          userSource = userSource,
          declslidesDependency = declslidesDependency,
          scalaVersion = scalaVersion,
        ),
      )

  private def fillTemplate(
    template: String,
    userSource: String,
    declslidesDependency: String,
    scalaVersion: Option[String],
  ): String =
    template
      .replace(ScalaDirectivePlaceholder, scalaDirective(scalaVersion))
      .replace(DependencyPlaceholder, declslidesDependency)
      .replace(UserSourcePlaceholder, indent(userSource, UserSourceIndent))

  private def scalaDirective(
    scalaVersion: Option[String],
  ): String =
    scalaVersion.fold("")(version => s"//> using scala $version\n")

  private def indent(
    value: String,
    spaces: Int,
  ): String =
    val padding =
      " " * spaces

    value.linesIterator
      .map(line => s"$padding$line")
      .mkString("\n")
