package datagen.custom

import com.google.gson.JsonArray
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
          "pools" to JsonArray()
        },
        "loot_tables/advancements",
        "data"
      )
    )
  }

  fun cellCrafterRecipe(
    name: String,
    priority: Int,
    category: String,
    inputs: List<Pair<String, Int>>,
    output: Pair<String, Int>
  ) = Preset {
    add(name, JsonResource(
      json {
        "type" to "minecells:cell_forge_recipe"
        "priority" to priority
        "category" to category
        "input" {
          inputs.forEach { (id, count) ->
            id to count
          }
        }
        "output" {
          "id" to output.first
          "Count" to output.second
        }
      },
      "recipes/",
      "data"
    ))
  }
}