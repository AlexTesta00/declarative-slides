package cli

import declslides.cli.DeclSlidesRuntime
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DeclSlidesRuntimeSpec extends AnyFlatSpec with Matchers:

  behavior of "DeclSlidesRuntime"

  it should "define a non blank core dependency coordinate" in:
    DeclSlidesRuntime.coreDependency.trim should not be empty
    DeclSlidesRuntime.coreDependency should include("::")

  it should "define a scala version only as a non blank value" in:
    DeclSlidesRuntime.scalaVersion.foreach { version =>
      version.trim should not be empty
    }

  it should "use a Scala 3 version when a version is configured" in:
    DeclSlidesRuntime.scalaVersion.foreach { version =>
      version should startWith("3.")
    }
