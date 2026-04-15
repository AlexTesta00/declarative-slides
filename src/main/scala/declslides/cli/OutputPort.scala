package declslides.cli

trait OutputPort:
  def writeLine(line: String): Unit
