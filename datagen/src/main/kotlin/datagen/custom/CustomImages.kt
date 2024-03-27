package datagen.custom

import datagen.getImageFile
import tada.lib.image.ImageAtlas
import java.nio.file.Path

object CustomImages {
  fun createBowTextures(
    atlasName: String,
    outputDirectory: Path,
  ) = ImageAtlas.createAndSave(
    getImageFile(atlasName),
    outputDirectory.resolve("assets/minecells/textures/item/bow").toFile(),
    spriteWidth = 32,
    spriteHeight = 32
  ) {
    val states = listOf("", "_pulling_0", "_pulling_1", "_pulling_2")
    states.forEach { state ->
      Constants.BOWS.forEach { bow ->
        sprite("$bow${state}")
      }
    }
  }
}