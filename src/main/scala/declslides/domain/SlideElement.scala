package declslides.domain

type URL = String

/** Content elements that can appear inside a slide. */
enum SlideElement derives CanEqual:
  /** Plain paragraph text. */
  case Paragraph(value: String)

  /** Bullet list content. */
  case BulletList(items: Vector[String])

  /** Source code block with an associated language label. */
  case CodeBlock(
    language: String,
    source: String)

  /** Vertical spacer measured in logical lines. */
  case Spacer(lines: Int)

  /** An image with a source URL and alternative text. */
  case Image(
    source: URL,
    altText: String)

/** Validation helpers for [[SlideElement]] values. */
object SlideElement:

  /** Validates a single slide element.
    *
    * Validation is local to the element and returns every error found for that
    * value.
    */
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

    case SlideElement.Image(source, altText) =>
      valideteImage(source, altText)

  private def valideteImage(
    source: URL,
    altText: String,
  ): Vector[DomainError] =
    Option.when(source.trim.isEmpty)(DomainError.EmptyImageSource).toVector ++
      Option.when(altText.trim.isEmpty)(DomainError.EmptyImageAltText).toVector
