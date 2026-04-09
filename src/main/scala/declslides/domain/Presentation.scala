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
    val normalizedTitle =
      title.trim

    val errors =
      validateSkeleton(
        title = normalizedTitle,
        slideTitles = slides.map(_.title),
      )

    Either.cond(
      errors.isEmpty,
      new Presentation(normalizedTitle, slides, theme),
      errors,
    )

  def validateSkeleton(
    title: String,
    slideTitles: Vector[String],
  ): Vector[DomainError] =
    val normalizedTitle =
      title.trim

    val duplicates =
      slideTitles
        .map(_.trim)
        .groupBy(identity)
        .collect {
          case (slideTitle, sameTitles)
              if slideTitle.nonEmpty && sameTitles.size > 1 =>
            slideTitle
        }
        .toVector
        .sorted

    Option.when(normalizedTitle.isEmpty)(
      DomainError.EmptyPresentationTitle,
    ).toVector ++
      Option.when(slideTitles.isEmpty)(
        DomainError.PresentationWithoutSlides,
      ).toVector ++
      Option.when(duplicates.nonEmpty)(
        DomainError.DuplicateSlideTitles(duplicates),
      ).toVector
