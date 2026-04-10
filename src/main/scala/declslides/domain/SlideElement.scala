package declslides.domain

enum SlideElement derives CanEqual:
  case Paragraph(value: String)
  case BulletList(items: Vector[String])

  case CodeBlock(
    language: String,
    source: String)

  case Spacer(lines: Int)

object SlideElement:

  def validate(element: SlideElement): Vector[DomainError] = element match
    case SlideElement.Paragraph(value) =>
      Option.when(value.trim.isEmpty)(DomainError.EmptyParagraph).toVector

    case SlideElement.BulletList(items) if items.isEmpty =>
      Vector(DomainError.EmptyBulletList)

    case SlideElement.BulletList(items) =>
      items.zipWithIndex.collect:
        case (item, index) if item.trim.isEmpty =>
          DomainError.EmptyBulletItem(index)

    case SlideElement.CodeBlock(language, source) =>
      Option.when(
        language.trim.isEmpty,
      )(DomainError.EmptyCodeLanguage).toVector ++
        Option.when(source.trim.isEmpty)(DomainError.EmptyCodeBlock).toVector

    case SlideElement.Spacer(lines) =>
      Option.when(lines <= 0)(DomainError.NonPositiveSpacer(lines)).toVector
