package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.hardcoded.JsonResource
import tada.lib.util.Id
import tada.lib.util.json

object CustomHardcodedPresets {
  fun advancementDrop(advancement: String, item: String) = Preset {
    val (_, name) = Id(advancement)
    this.add(
      name,
      JsonResource(
        json {
          "pools" {
            "rolls" to 1
            "entries" to array[
              json {
                "type" to "minecraft:item"
                "name" to item
              }
            ]
          }
        },
        "advancements/$name",
      )
    )
  }
}