package declslides.domain

/** Fully validated presentation ready to be rendered.
  *
  * A `Presentation` guarantees a non-blank title, at least one slide, and a set
  * of slide titles that is unique after normalization.
  *
  * @param title
  *   normalized presentation title
  * @param slides
  *   validated slides in declaration order
  * @param theme
  *   theme used by renderers
  */
final case class Presentation private (
  title: String,
  slides: Vector[Slide],
  theme: Theme):

  /** Returns the titles of all slides in declaration order. */
  def slideTitles: Vector[String] =
    slides.map(_.title)

/** Factory and validation helpers for [[Presentation]]. */
object Presentation:

  /** Creates a validated presentation.
    *
    * @return
    *   the validated presentation, or all structural errors found
    */
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

  /** Validates presentation-level constraints without constructing the value.
    *
    * This is useful when slide validation is handled elsewhere and only the
    * outer structure still needs to be checked.
    */
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
