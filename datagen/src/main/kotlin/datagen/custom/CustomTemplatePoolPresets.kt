package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.common.TemplatePoolPresets as Presets

object CustomTemplatePoolPresets {
  fun prisonersQuarters() = Preset {
    val processors = "minecells:brick_decay"
    // Corridors
    add(Presets.single("minecells:prison/spawn", processors))
    add(Presets.indexed("minecells:prison/corridor", 1, 2, 2, processors = processors))
    add(Presets.indexed("minecells:prison/main_corridor", 2, 2, 1, processors = processors))
    add(Presets.indexedUnweighted("minecells:prison/main_corridor_end", 0, processors = processors))
    add(Presets.single("minecells:prison/main_corridor_end/0", processors, id = "minecells:prison/corridor_end"))
    // Floor changes
    add(Presets.indexedUnweighted("minecells:prison/chain_lower", 0, processors = processors))
    add(Presets.indexedUnweighted("minecells:prison/chain_upper", 0, processors = processors))
    // Exits
    add(Presets.single("minecells:prison/end", processors))
    add(Presets.single("minecells:prison/end_sewers", processors))
    // Decoration
    add(Presets.prefixed("minecells:prison/ceiling_decoration",
      "minecraft:empty" to 4, "broken_cage" to 4, "cage" to 4, "chains_0" to 2, "chains_1" to 2, "chains_2" to 2,
      "cobwebs_0" to 1, "cobwebs_1" to 1, "leaves_0" to 6, "leaves_1" to 6, "leaves_2" to 6, "stone_0" to 6,
      "stone_1" to 6, "stone_2" to 6
    ))
    add(Presets.prefixed("minecells:prison/corridor_decoration",
      "bars" to 2, "cobwebs" to 1, "crates_0" to 2, "crates_1" to 2, "crates_2" to 2, "crates_3" to 2,
      "shelves_0" to 2, "shelves_1" to 2, "chest" to 2
    ))
    add(Presets.indexedUnweighted("minecells:prison/spawn_decoration", 6))
    // Doorways
    add(Presets.indexedUnweighted("minecells:prison/main_corridor_doorway", 3))
    add(Presets.indexedUnweighted("minecells:prison/main_corridor_side_doorway", 3))
    add(Presets.prefixed("minecells:prison/main_corridor_doorway", "1" to 1, "2" to 1, "3" to 1, id = "minecells:prison/corridor_hole"))
    // Rooms
    add(Presets.indexed("minecells:prison/cell", 2, 1, 1, 1))
    add(Presets.indexedUnweighted("minecells:prison/corridor_hole_cell", 5, processors))
    // Runes and chests
    add(Presets.single("minecells:prison/spawn_rune_and_chest"))
    add(Presets.single("minecells:prison/spawn_rune"))
    add(Presets.single("minecells:prison/chest"))
  }
}