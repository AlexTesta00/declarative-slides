package declslides.application

import declslides.domain.Presentation

trait PresentationRegistry:
  def available: Vector[String]
  def resolve(name: String): Either[ApplicationError, Presentation]

final class InMemoryPresentationRegistry private (
  entries: Map[String, Presentation]) extends PresentationRegistry:

  override def available: Vector[String] =
    entries.keys.toVector.sorted

  override def resolve(name: String): Either[ApplicationError, Presentation] =
    entries.get(name).toRight(ApplicationError.PresentationNotFound(name))

object InMemoryPresentationRegistry:

  def apply(entries: (String, Presentation)*): InMemoryPresentationRegistry =
    new InMemoryPresentationRegistry(entries.toMap)
