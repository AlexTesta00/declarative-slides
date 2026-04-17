package declslides.cli

import declslides.application.ApplicationError
import declslides.application.FileSystem

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

final class JvmFileSystem extends FileSystem:

  override def write(
    path: String,
    content: String,
  ): Either[ApplicationError, Unit] =
    try
      val outputPath = Paths.get(path)
      val parent = outputPath.getParent

      Option(outputPath.getParent).foreach(parent =>
        Files.createDirectories(parent),
      )

      Files.writeString(outputPath, content, StandardCharsets.UTF_8)
      Right(())
    catch
      case exception: Exception =>
        Left(ApplicationError.WriteFailure(path, exception.getMessage))
