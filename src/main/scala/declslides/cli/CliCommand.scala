package declslides.cli

import declslides.rendering.RenderingTarget

enum CliCommand derives CanEqual:
  case Help
  case ListPresentations

  case Render(
    name: String,
    format: RenderingTarget)
