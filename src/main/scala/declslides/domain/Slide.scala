package declslides.domain

final case class Slide private (
  title: String,
  elements: Vector[SlideElement],
  layout: Layout)

object Slide:

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
