package declslides.domain

final case class Presentation private (
    title: String,
    slides: Vector[Slide],
    theme: Theme
):
  def slideTitles: Vector[String] =
    slides.map(_.title)

object Presentation:
  def create(
      title: String,
      slides: Vector[Slide],
      theme: Theme = Theme.default
  ): Either[Vector[DomainError], Presentation] =
    var errors = Vector.empty[DomainError]
    val t      = title.trim

    if t.isEmpty then errors = errors :+ DomainError.EmptyPresentationTitle

    if slides.isEmpty then
      errors = errors :+ DomainError.PresentationWithoutSlides

    val grouped    = slides.groupBy(_.title)
    val duplicates =
      grouped.collect { case (name, xs) if xs.size > 1 => name }.toVector.sorted

    if duplicates.nonEmpty then
      errors = errors :+ DomainError.DuplicateSlideTitles(duplicates)

    if errors.isEmpty then Right(Presentation(t, slides, theme))
    else Left(errors)
