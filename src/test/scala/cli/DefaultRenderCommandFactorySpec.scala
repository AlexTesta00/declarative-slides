package cli

import declslides.cli.CliError
import declslides.cli.DeclSlidesRuntime
import declslides.cli.DefaultRenderCommandFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DefaultRenderCommandFactorySpec extends AnyFlatSpec with Matchers:

  behavior of "DefaultRenderCommandFactory"

  it should
    "mirror the runtime scala cli availability when creating the default command" in:
      val result = DefaultRenderCommandFactory.create()

      DeclSlidesRuntime.scalaCliBinary match
        case Left(error) =>
          result shouldBe Left(CliError.RuntimeInitialization(error))

        case Right(_) =>
          result.isRight shouldBe true
