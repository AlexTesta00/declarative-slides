package declslides.domain

/** Visual layout hints that renderers can use for a slide.
  *
  * Layouts are intentionally few and generic so that different renderers can
  * interpret them consistently.
  */
enum Layout derives CanEqual:
  /** Standard top-to-bottom content flow. */
  case Flow

  /** Centered content, useful for title or focus slides. */
  case Centered
