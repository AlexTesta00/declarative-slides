package declslides.domain

type ThemeName = String
type HexColor = String

final case class Theme(
  name: ThemeName,
  background: HexColor,
  foreground: HexColor,
  accent: HexColor,
  codeBackground: HexColor)

object Theme:

  val default: Theme =
    Theme(
      name = "default",
      background = "#FFFFFF",
      foreground = "#111111",
      accent = "#0057B8",
      codeBackground = "#F3F4F6",
    )

  val light: Theme =
    default.copy(name = "light")

  val dark: Theme =
    Theme(
      name = "dark",
      background = "#0B0B0C",
      foreground = "#FAFAFA",
      accent = "#00E5FF",
      codeBackground = "#16181D",
    )

  val conference: Theme =
    Theme(
      name = "conference",
      background = "#000000",
      foreground = "#FFFFFF",
      accent = "#FFD60A",
      codeBackground = "#111111",
    )

  val presets: Vector[Theme] =
    Vector(default, light, dark, conference)
