package declslides.application

import declslides.rendering.RenderFormat
import declslides.rendering.RendererRegistry

trait RenderFormatResolver:
  def resolve(rawFormat: String): Either[ApplicationError, RenderFormat]

final class RegistryRenderFormatResolver(
  registry: RendererRegistry) extends RenderFormatResolver:

  override def resolve(
    rawFormat: String,
  ): Either[ApplicationError, RenderFormat] =
    registry
      .parse(rawFormat)
      .toRight(
        ApplicationError.UnsupportedFormat(
          raw = rawFormat,
          supported = registry.supportedLabels,
        ),
      )
