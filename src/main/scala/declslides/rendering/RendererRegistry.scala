package declslides.rendering

trait RendererRegistry:
  def available: Vector[Renderer]

  def resolve(target: RenderFormat): Option[Renderer]

  def parse(raw: String): Option[RenderFormat]

  def supportedLabels: Vector[String]

object RendererRegistry:

  def apply(renderers: Renderer*): RendererRegistry =
    InMemoryRendererRegistry(renderers.toVector)

private final class InMemoryRendererRegistry(
  renderers: Vector[Renderer]) extends RendererRegistry:

  private val formats: Vector[RenderFormat] =
    renderers.map(_.target).distinctBy(_.label)

  override def available: Vector[Renderer] =
    renderers

  override def resolve(target: RenderFormat): Option[Renderer] =
    renderers.find(_.target == target)

  override def parse(raw: String): Option[RenderFormat] =
    formats.find(_.accepts(raw))

  override def supportedLabels: Vector[String] =
    formats.map(_.label)
