package declslides.domain

final case class Slide private (
    title: String,
    elements: Vector[SlideElement],
    layout: Layout
)

object Slide:
  def create(
      title: String,
      elements: Vector[SlideElement],
      layout: Layout = Layout.Flow
  ): Either[Vector[DomainError], Slide] =
    var errors = Vector.empty[DomainError]
    val t      = title.trim

    if t.isEmpty then errors = errors :+ DomainError.EmptySlideTitle

    if elements.isEmpty then
      errors = errors :+ DomainError.SlideWithoutElements(t)

    elements.foreach {
      case SlideElement.Paragraph(value) =>
        if value.trim.isEmpty then errors = errors :+ DomainError.EmptyParagraph

      case SlideElement.BulletList(items) =>
        if items.isEmpty then errors = errors :+ DomainError.EmptyBulletList
        else
          items.zipWithIndex.foreach { case (item, index) =>
            if item.trim.isEmpty then
              errors = errors :+ DomainError.EmptyBulletItem(index)
          }

      case SlideElement.CodeBlock(language, source) =>
        if language.trim.isEmpty then
          errors = errors :+ DomainError.EmptyCodeLanguage
        if source.trim.isEmpty then
          errors = errors :+ DomainError.EmptyCodeBlock

      case SlideElement.Spacer(lines) =>
        if lines <= 0 then
          errors = errors :+ DomainError.NonPositiveSpacer(lines)
    }

    if errors.isEmpty then Right(Slide(t, elements, layout))
    else Left(errors)
