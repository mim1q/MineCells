package datagen

import tada.lib.generator.FilesystemFileSaver
import tada.lib.generator.ResourceGenerator
import java.nio.file.Path

class SelectiveFileSaver(
  private vararg val excluded: String
) : ResourceGenerator.FileSaver by FilesystemFileSaver {
  override fun save(filePath: Path, content: String) {
    val normalizedPath = filePath.toString().replace('\\', '/')
    if (excluded.none { Regex(it).matches(normalizedPath) }) {
      FilesystemFileSaver.save(filePath, content)
    } else {
      println("Excluding file: ${filePath.normalize()}")
    }
  }
}