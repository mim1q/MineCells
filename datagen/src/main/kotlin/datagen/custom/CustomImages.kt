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
    val weapons = Constants.BOWS + Constants.CROSSBOWS
    states.forEach { state ->
      weapons.forEach { bow ->
        sprite("$bow${state}")
      }
    }
  }

  fun createFlagTextures(
    atlasName: String,
    outputDirectory: Path,
  ) {
    ImageAtlas.createAndSave(
      getImageFile(atlasName),
      outputDirectory.resolve("assets/minecells/textures/blockentity/banner/").toFile(),
      spriteWidth = 64,
      spriteHeight = 64
    ) {
      Constants.COLORS.forEach { color ->
        sprite("${color}_ribbon")
      }
    }

    ImageAtlas.createAndSave(
      getImageFile("large_${atlasName}"),
      outputDirectory.resolve("assets/minecells/textures/blockentity/banner/").toFile(),
      spriteWidth = 64,
      spriteHeight = 128
    ) {
      Constants.COLORS.forEach { color ->
        sprite("large_${color}_ribbon")
      }
    }
  }
}