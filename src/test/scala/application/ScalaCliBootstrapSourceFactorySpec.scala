package application

import declslides.application.ApplicationError
import declslides.application.ScalaCliBootstrapSourceFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaCliBootstrapSourceFactorySpec extends AnyFlatSpec with Matchers:

  behavior of "ScalaCliBootstrapSourceFactory"

  private def expectRight(
    result: Either[ApplicationError, String],
  ): String =
    result match
      case Right(source) =>
        source

      case Left(error) =>
        fail(s"Expected Right(source), got Left($error)")

  it should "include the declslides dependency directive" in:
    val source =
      expectRight(
        ScalaCliBootstrapSourceFactory.create(
          userSource = "Right(presentation)",
          declslidesDependency = "com.acme::declslides-core:1.0.0",
          scalaVersion = None,
        ),
      )

    source should include("//> using dep com.acme::declslides-core:1.0.0")

  it should "include the scala directive when a scala version is provided" in:
    val source =
      expectRight(
        ScalaCliBootstrapSourceFactory.create(
          userSource = "Right(presentation)",
          declslidesDependency = "com.acme::declslides-core:1.0.0",
          scalaVersion = Some("3.3.3"),
        ),
      )

    source should include("//> using scala 3.3.3")

  it should "omit the scala directive when no scala version is provided" in:
    val source =
      expectRight(
        ScalaCliBootstrapSourceFactory.create(
          userSource = "Right(presentation)",
          declslidesDependency = "com.acme::declslides-core:1.0.0",
          scalaVersion = None,
        ),
      )

    source should not include "//> using scala"

  it should "embed the user source inside the bootstrap resolve block" in:
    val userSource =
      """Right(
        |  presentation
        |)""".stripMargin

    val source =
      expectRight(
        ScalaCliBootstrapSourceFactory.create(
          userSource = userSource,
          declslidesDependency = "com.acme::declslides-core:1.0.0",
          scalaVersion = None,
        ),
      )

    source should include("Bootstrap.resolve {")
    source should include("      Right(")
    source should include("        presentation")
    source should include("      )")

  it should
    "define a render main entrypoint in the generated bootstrap source" in:
      val source =
        expectRight(
          ScalaCliBootstrapSourceFactory.create(
            userSource = "Right(presentation)",
            declslidesDependency = "com.acme::declslides-core:1.0.0",
            scalaVersion = None,
          ),
        )

      source should
        include("@main def render(format: String, output: String): Unit =")
      source should include("DefaultRendererRegistry.live")
      source should include("Files.writeString")
