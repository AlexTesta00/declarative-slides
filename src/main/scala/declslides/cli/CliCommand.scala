package declslides.cli

import declslides.application.RenderFormat

enum CliCommand derives CanEqual:
  case Help
  case ListPresentations

  case Render(
    name: String,
    format: RenderFormat)
