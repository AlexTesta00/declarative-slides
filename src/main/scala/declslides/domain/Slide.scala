package declslides.domain

/** Fully validated slide ready to be rendered.
  *
  * A `Slide` guarantees a non-blank title and at least one valid element.
  *
  * @param title
  *   normalized slide title
  * @param elements
  *   validated slide elements in declaration order
  * @param layout
  *   rendering hint for slide layout
  */
final case class Slide private (
  title: String,
  elements: Vector[SlideElement],
  layout: Layout)

/** Factory for validated [[Slide]] values. */
object Slide:

  /** Creates a validated slide.
    *
    * @return
    *   the validated slide, or every validation error found for its title and
    *   elements
    */
  def apply(
    title: String,
    elements: Vector[SlideElement],
    layout: Layout = Layout.Flow,
  ): Either[Vector[DomainError], Slide] =
    val normalizedTitle = title.trim
    val errors =
      Option.when(normalizedTitle.isEmpty)(
        DomainError.EmptySlideTitle,
      ).toVector ++
        Option.when(elements.isEmpty)(
          DomainError.SlideWithoutElements(normalizedTitle),
        ).toVector ++
        elements.flatMap(SlideElement.validate)

    Either.cond(
      errors.isEmpty,
      new Slide(normalizedTitle, elements, layout),
      errors,
    )
