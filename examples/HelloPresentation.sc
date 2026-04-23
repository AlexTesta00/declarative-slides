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
          "Spaziature",
          "Immagini"
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
    },
    slide("Media", Flow) {
      content(
        text("Ecco un esempio di immagine:"),
        image(
          "https://images.unsplash.com/photo-1776722091903-097d6dab0455?q=80&w=1287&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "Google logo"
        )
      )
    }
  )
}