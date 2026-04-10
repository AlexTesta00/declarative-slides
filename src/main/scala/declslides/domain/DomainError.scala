package declslides.domain

enum DomainError derives CanEqual:
  case EmptyPresentationTitle
  case PresentationWithoutSlides
  case DuplicateSlideTitles(titles: Vector[String])
  case EmptySlideTitle
  case SlideWithoutElements(title: String)
  case EmptyParagraph
  case EmptyBulletList
  case EmptyBulletItem(index: Int)
  case EmptyCodeLanguage
  case EmptyCodeBlock
  case NonPositiveSpacer(lines: Int)

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
