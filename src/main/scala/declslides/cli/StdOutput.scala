package declslides.cli

object StdOutput extends OutputPort:

  override def writeLine(line: String): Unit =
    println(line)
