package declslides.rendering.text

import declslides.domain.Presentation
import declslides.domain.Slide

object TextRendererFormatting:

  def renderTitle(presentation: Presentation): String =
    presentation.title

  def renderTheme(presentation: Presentation): String =
    s"Theme: ${presentation.theme.name}"

  def renderSlideHeader(
    slide: Slide,
    number: Int,
  ): String =
    s"[$number] ${slide.title} (${slide.layout})"

  def renderParagraph(value: String): Seq[String] =
    Seq(value)

  def renderBulletList(items: Seq[String]): Seq[String] =
    items.map(renderBullet)

  private def renderBullet(item: String): String =
    s"- $item"

  def renderCodeBlock(
    language: String,
    source: String,
  ): Seq[String] =
    Seq(
      s"```$language",
      source,
      "```",
    )

  def renderSpacer(lines: Int): Seq[String] =
    List.fill(lines)("")
