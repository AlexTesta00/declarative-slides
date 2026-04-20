package declslides.rendering.html

import declslides.domain.Theme

object HtmlRendererStyles:

  def render(theme: Theme): String =
    s"""
       |:root {
       |  --background: ${theme.background};
       |  --foreground: ${theme.foreground};
       |  --accent: ${theme.accent};
       |  --code-background: ${theme.codeBackground};
       |  --border: rgba(255, 255, 255, 0.15);
       |}
       |
       |* {
       |  box-sizing: border-box;
       |}
       |
       |html {
       |  scroll-behavior: smooth;
       |}
       |
       |body {
       |  margin: 0;
       |  font-family: system-ui, sans-serif;
       |  background: var(--background);
       |  color: var(--foreground);
       |}
       |
       |.presentation {
       |  max-width: 1100px;
       |  margin: 0 auto;
       |  padding: 2rem;
       |}
       |
       |.deck-header {
       |  border-bottom: 2px solid var(--accent);
       |  margin-bottom: 2rem;
       |  padding-bottom: 1rem;
       |}
       |
       |.slide {
       |  min-height: 70vh;
       |  padding: 2rem 0;
       |  border-bottom: 1px solid var(--border);
       |  display: flex;
       |  flex-direction: column;
       |  gap: 1rem;
       |}
       |
       |.slide.centered {
       |  justify-content: center;
       |  align-items: center;
       |  text-align: center;
       |}
       |
       |.slide h2 {
       |  margin: 0 0 1rem 0;
       |  color: var(--accent);
       |}
       |
       |p, ul, pre {
       |  margin: 0;
       |}
       |
       |ul {
       |  line-height: 1.7;
       |  padding-left: 1.25rem;
       |}
       |
       |pre {
       |  background: var(--code-background);
       |  padding: 1rem;
       |  border-radius: 8px;
       |  overflow-x: auto;
       |}
       |
       |code {
       |  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
       |}
       |""".stripMargin
