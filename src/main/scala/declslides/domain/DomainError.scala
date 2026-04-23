package declslides.domain

/** Validation errors raised by the presentation domain model.
  *
  * Domain errors describe structural and content-level problems in a deck, such
  * as missing titles, empty content, duplicate slide names, or invalid slide
  * elements.
  */
enum DomainError derives CanEqual:
  /** A presentation must have a non-empty title. */
  case EmptyPresentationTitle

  /** A presentation must contain at least one slide. */
  case PresentationWithoutSlides

  /** Slide titles must be unique within a presentation. */
  case DuplicateSlideTitles(titles: Vector[String])

  /** A slide must have a non-empty title. */
  case EmptySlideTitle

  /** A slide must contain at least one element (paragraph, bullet list, code
    * block, image, or spacer).
    */
  case SlideWithoutElements(title: String)

  /** A paragraph must have non-empty text. */
  case EmptyParagraph

  /** A bullet list must contain at least one item. */
  case EmptyBulletList

  /** A bullet item must have non-empty text. */
  case EmptyBulletItem(index: Int)

  /** A code block must have a non-empty language. */
  case EmptyCodeLanguage

  /** A code block must have non-empty source code. */
  case EmptyCodeBlock

  /** A spacer must have a positive number of lines. */
  case NonPositiveSpacer(lines: Int)

  /** An image must have a non-empty source URL. */
  case EmptyImageSource

  /** An image must have non-empty alt text. */
  case EmptyImageAltText

  /** Returns a human-readable explanation of the validation problem. */
  def message: String = this match
    case EmptyPresentationTitle =>
      "Presentation title must not be blank"
    case PresentationWithoutSlides =>
      "Presentation must contain at least one slide"
    case DuplicateSlideTitles(titles) =>
      s"Slide titles must be unique. Duplicates: ${titles.mkString(", ")}"
    case EmptySlideTitle =>
      "Slide title must not be blank"
    case SlideWithoutElements(title) =>
      s"Slide '$title' must contain at least one element"
    case EmptyParagraph =>
      "Paragraph text must not be blank"
    case EmptyBulletList =>
      "Bullet list must contain at least one item"
    case EmptyBulletItem(index) =>
      s"Bullet item at index $index must not be blank"
    case EmptyCodeLanguage =>
      "Code block language must not be blank"
    case EmptyCodeBlock =>
      "Code block source must not be blank"
    case NonPositiveSpacer(lines) =>
      s"Spacer lines must be positive, found $lines"
    case EmptyImageSource =>
      "Image source must not be blank"
    case EmptyImageAltText =>
      "Image alt text must not be blank"
