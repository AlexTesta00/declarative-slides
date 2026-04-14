package declslides.application

import declslides.domain.Presentation

trait PresentationRegistry:
  def available: Vector[String]
  def resolve(name: String): Either[ApplicationError, Presentation]

final class InMemoryPresentationRegistry(
  entries: Map[String, Presentation]) extends PresentationRegistry:

  def available: Vector[String] =
    entries.keys.toVector.sorted

  def resolve(name: String): Either[ApplicationError, Presentation] =
    entries.get(name) match
      case Some(presentation) => Right(presentation)
      case None => Left(ApplicationError.PresentationNotFound(name))

object InMemoryPresentationRegistry:

  def apply(entries: (String, Presentation)*): InMemoryPresentationRegistry =
    new InMemoryPresentationRegistry(entries.toMap)
