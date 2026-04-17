# Declarative-Slides

The project implements a **Scala 3 embedded DSL** for defining slide presentations in a declarative and compositional style, with support for:

- **Domain Validation**
- **Text Rendering**
- **HTML Rendering**
- **Application-level Rendering Use Cases**
- **CLI execution**

## Main features
- Support for:
    - paragraphs
    - bullet lists
    - code blocks
    - spacers
- Slide layouts:
    - `Flow`
    - `Centered`
- Themes:
    - `default`
    - `light`
    - `conference`
- Rendering targets:
    - `text`
    - `html`
- CLI commands:
    - `help`
    - `list`
    - `render <presentation-name> <format> [output-path]`