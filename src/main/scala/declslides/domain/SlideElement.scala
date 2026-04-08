package declslides.domain

enum SlideElement:
  case Paragraph(value: String)
  case BulletList(items: Vector[String])
  case CodeBlock(language: String, source: String)
  case Spacer(lines: Int)
