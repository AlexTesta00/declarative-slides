package declslides.domain

final case class Theme(
  name: String,
  background: String,
  foreground: String,
  accent: String,
  codeBackground: String)

object Theme:
  val default = Theme("default", "#101418", "#F5F7FA", "#4DD0E1", "#1B2530")
  val light = Theme("light", "#FFFFFF", "#1F2933", "#3366FF", "#F4F7FB")

  val conference =
    Theme("conference", "#0B132B", "#FAF9F6", "#FFB703", "#1C2541")
