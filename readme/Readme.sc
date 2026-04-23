import declslides.domain.Layout.Flow
import declslides.domain.Theme
import declslides.dsl.DSL._

presentation("Declerative-Slides").use(Theme.default) {
  deck(
    slide("Declarative-Slides", Flow){
      content(
        text("The project implements a Scala 3 embedded DSL for defining slide presentations in a declarative and compositional style, with support for:"),
        bullets(
            "Text",
            "Code snippets",
            "Bullet lists",
            "Spacing",
            "Images"
        )
      )
    },
    slide("Example", Flow){
      content(
        text("Here is an example of code:"),
        code(
          "scala",
          """
            |presentation("Hello DeclSlides").use(Theme.default) {
            |  deck(
            |    slide("Intro", Flow) {
            |      content(
            |        text("This is the declerative slides tool"),
            |        text("Here you can write presentations in a declarative way through a DSL in scala"),
            |        text("The DSL supports: "),
            |        bullets(
            |          "Texts",
            |          "Code snippets",
            |          "Bullet lists",
            |          "Spacing",
            |          "Images"
            |          )
            |       )
            |   },
            |}
            |"""
        )
      )
    },
    slide("How to setup", Flow){
      content(
        text("Add to path variables:"),
        code(
          "bash",
            """DECLSLIDES_SCALA_CLI=path/to/scala-cli"""
        ),
        text("Then run:"),
        code(
          "bash",
            """java -jar declslides.jar --input path/to/presentation.sc --format html --output path/to/presentation.html"""
        )
      )
    }
  )
}