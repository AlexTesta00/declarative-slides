package declslides.rendering

/** Registry of available renderers.
  *
  * A renderer registry is responsible for three small but important tasks:
  * listing renderers, resolving a renderer from a concrete target, and parsing
  * a raw format label into a known rendering target.
  */
trait RendererRegistry:

  /** Returns all renderers registered in this registry. */
  def available: Vector[Renderer]

  /** Resolves the renderer responsible for the given target. */
  def resolve(target: RenderFormat): Option[Renderer]

  /** Parses a raw format label into a know rendering target. */
  def parse(raw: String): Option[RenderFormat]

  /** Returns canonical labels of all supported targets. */
  def supportedLabels: Vector[String]

/* Factory for in-memory renderer registry. */
object RendererRegistry:

  /** Build a registry from the provided renderers. */
  def apply(renderers: Renderer*): RendererRegistry =
    InMemoryRendererRegistry(renderers.toVector)

/** Simple in-memory implementation of [[RendererRegistry]] */
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
