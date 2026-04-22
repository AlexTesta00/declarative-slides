package declslides.application

import declslides.rendering.RenderFormat
import declslides.rendering.RendererRegistry

/** Resolves a raw format label into a concrete rendering target.
  *
  * This abstraction separates format lookup from the render use case itself,
  * making the command easier to test and easier to extend.
  */
trait RenderFormatResolver:

  /** Resolves a raw format label into a known
    * [[declslides.rendering.RenderFormat]].
    */
  def resolve(rawFormat: String): Either[ApplicationError, RenderFormat]

/** Format resolver backed by a [[declslides.rendering.RendererRegistry]]. */
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
