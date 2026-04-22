import declslides.domain.Theme
import declslides.dsl.DSL._

presentation("Hello DeclSlides").use(Theme.default) {
  deck(
    slide("Intro") {
      content(
        text("Questa presentazione arriva da uno script .sc"),
        bullets(
          "DSL pura",
          "render HTML",
          "render text"
        )
      )
    },
    slide("Code") {
      content(
        code(
          "scala",
          """println("hello declslides")"""
        )
      )
    }
  )
}