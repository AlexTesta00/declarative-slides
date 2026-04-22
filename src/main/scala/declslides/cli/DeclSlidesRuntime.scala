package declslides.cli

object DeclSlidesRuntime:

  private val ScalaCliEnvVar: String =
    "DECLSLIDES_SCALA_CLI"

  val coreDependency: String =
    "com.alextesta::declerative-slides:0.1.0-SNAPSHOT"

  val scalaVersion: Option[String] =
    Some("3.8.3")

  def scalaCliBinary: Either[String, String] =
    sys.env
      .get(ScalaCliEnvVar)
      .map(_.trim)
      .filter(_.nonEmpty)
      .toRight(
        s"Missing environment variable '$ScalaCliEnvVar'. " +
          s"Set it to the full path of your Scala CLI executable.",
      )
