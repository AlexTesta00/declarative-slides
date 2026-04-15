package declslides.cli

import declslides.rendering.RenderFormat

enum CliCommand derives CanEqual:
  case Help
  case ListPresentations

  case Render(
    name: String,
    format: RenderFormat)
