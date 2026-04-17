package declslides.rendering.html

import declslides.domain.Theme
import scalacss.DevDefaults._

object HtmlRendererStyles:

  def render(theme: Theme): String =
    object Styles extends StyleSheet.Standalone:
      import dsl.*

      "body" - (
        margin.`0`,
        fontFamily :=! "system-ui, sans-serif",
        backgroundColor :=! theme.background,
        color :=! theme.foreground,
      )

      ".presentation" - (
        maxWidth(1100.px),
        margin(0.px, auto),
        padding(2.rem),
      )

      ".deck-header" - (
        borderBottom :=! s"2px solid ${theme.accent}",
        marginBottom(2.rem),
        paddingBottom(1.rem),
      )

      ".slide" - (
        minHeight :=! "70vh",
        paddingTop(2.rem),
        paddingBottom(2.rem),
        borderBottom :=! "1px solid rgba(255,255,255,0.15)",
        display.flex,
        flexDirection.column,
        gap :=! "1rem",
      )

      ".slide.centered" - (
        justifyContent.center,
        alignItems.center,
        textAlign.center,
      )

      ".slide h2" - (
        color :=! theme.accent,
        marginBottom(1.rem),
      )

      "pre" - (
        backgroundColor :=! theme.codeBackground,
        padding(1.rem),
        borderRadius(8.px),
        overflowX.auto,
      )

      "code" - (
        fontFamily :=! "ui-monospace, monospace"
      )

      "ul" - lineHeight(1.7)

    Styles.render
