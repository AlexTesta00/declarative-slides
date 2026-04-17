package declslides.cli

import declslides.rendering.RenderFormat

type Url = String

enum CliCommand derives CanEqual:
  case Help
  case ListPresentations

  case Render(
    name: String,
    format: RenderFormat,
    outputPath: Option[Url] = None)
