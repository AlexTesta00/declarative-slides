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
      background = "#101418",
      foreground = "#F5F7FA",
      accent = "#4DD0E1",
      codeBackground = "#1B2530",
    )

  val light: Theme =
    Theme(
      name = "light",
      background = "#FFFFFF",
      foreground = "#1F2933",
      accent = "#3366FF",
      codeBackground = "#F4F7FB",
    )

  val conference: Theme =
    Theme(
      name = "conference",
      background = "#0B132B",
      foreground = "#FAF9F6",
      accent = "#FFB703",
      codeBackground = "#1C2541",
    )

  val presets: Vector[Theme] =
    Vector(default, light, conference)
