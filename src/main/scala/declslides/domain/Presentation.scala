package declslides.domain

final case class Presentation private (
  title: String,
  slides: Vector[Slide],
  theme: Theme):

  def slideTitles: Vector[String] =
    slides.map(_.title)

object Presentation:

  def apply(
    title: String,
    slides: Vector[Slide],
    theme: Theme = Theme.default,
  ): Either[Vector[DomainError], Presentation] =
    val normalizedTitle = title.trim
    val duplicates =
      slides
        .groupBy(_.title)
        .collect {
          case (slideTitle, sameTitleSlides) if sameTitleSlides.size > 1 =>
            slideTitle
        }
        .toVector
        .sorted

    val errors =
      Option.when(normalizedTitle.isEmpty)(
        DomainError.EmptyPresentationTitle,
      ).toVector ++
        Option.when(slides.isEmpty)(
          DomainError.PresentationWithoutSlides,
        ).toVector ++
        Option.when(duplicates.nonEmpty)(
          DomainError.DuplicateSlideTitles(duplicates),
        ).toVector

    Either.cond(
      errors.isEmpty,
      new Presentation(normalizedTitle, slides, theme),
      errors,
    )
