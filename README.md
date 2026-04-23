## Declarative-Slides

The project implements a Scala 3 embedded DSL for defining slide presentations in a declarative and compositional style, with support for:

- Text
- Code snippets
- Bullet lists
- Spacing
- Images

## Example

Here is an example of code:

```scala

presentation("Hello DeclSlides").use(Theme.default) {
  deck(
    slide("Intro", Flow) {
      content(
        text("This is the declerative slides tool"),
        text("Here you can write presentations in a declarative way through a DSL in scala"),
        text("The DSL supports: "),
        bullets(
          "Texts",
          "Code snippets",
          "Bullet lists",
          "Spacing",
          "Images"
        )
      )
  },
}
                  
```

## How to setup

Add to path variables:

```bash
DECLSLIDES_SCALA_CLI=path/to/scala-cli
```

Then run:

```bash
java -jar declslides.jar --input path/to/presentation.sc --format html --output path/to/presentation.html
```