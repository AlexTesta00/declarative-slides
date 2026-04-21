package application

import declslides.application.ApplicationError
import declslides.application.RegistryRenderFormatResolver
import declslides.rendering.DefaultRendererRegistry
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RegistryRenderFormatResolverSpec extends AnyFlatSpec with Matchers:

  behavior of "RegistryRenderFormatResolver"

  it should "resolve a supported format using the renderer registry" in:
    val registry = DefaultRendererRegistry.live
    val resolver = new RegistryRenderFormatResolver(registry)
    val supportedFormat = registry.supportedLabels.head

    val result =
      resolver.resolve(supportedFormat)

    result.isRight shouldBe true

  it should "fail with an application error when the format is unsupported" in:
    val registry = DefaultRendererRegistry.live
    val resolver = new RegistryRenderFormatResolver(registry)

    val result =
      resolver.resolve("pdf")

    result shouldBe Left(
      ApplicationError.UnsupportedFormat(
        raw = "pdf",
        supported = registry.supportedLabels,
      ),
    )
