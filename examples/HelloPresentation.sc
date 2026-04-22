import declslides.domain.Layout.Flow
import declslides.domain.Theme
import declslides.dsl.DSL._

presentation("Hello DeclSlides").use(Theme.default) {
  deck(
    slide("Intro", Flow) {
      content(
        text("Questo è il tool declerative slides"),
        text("Qui puoi scrivere presentazioni in modo dichiarativo attraverso un DSL in scala"),
        text("Il DSL supporta: "),
        bullets(
          "Testi",
          "Pezzi di codice",
          "Liste puntate",
          "Spaziature"
        )
      )
    },
    slide("Code", Flow) {
      content(
        text("Ecco un esempio di codice:"),
        code(
          "scala",
          """println("hello declslides")"""
        )
      )
    }
  )
}