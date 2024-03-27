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
    val bows = listOf(
      "bow_and_endless_quiver",
      "explosive_crossbow",
      "heavy_crossbow",
      "ice_bow",
      "infantry_bow",
      "marksmans_bow",
      "multiple_nocks_bow",
      "nerves_of_steel",
      "quick_bow",
    )

    states.forEach { state ->
      bows.forEach { bow ->
        sprite("$bow${state}")
      }
    }
  }
}