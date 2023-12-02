package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.common.TemplatePoolPresets as Presets

object ModTemplatePools {
  fun common() = Preset {
    add(Presets.prefixed("minecells:common/corpse", "skeleton" to 1, "rotting_corpse" to 1, "corpse" to 1))
    add(Presets.prefixed("minecells:common/hanged_corpse", "skeleton" to 1, "rotting_corpse" to 1, "corpse" to 1))
    add(Presets.prefixed("minecells:common/standing_cage", "cage" to 1, "broken_cage" to 1))
    add(Presets.single("minecells:common/support_beam"))
  }

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

  fun promenadeOfTheCondemned() = Preset {
    val proc = "minecells:promenade"
    // Spawn
    add(Presets.single("minecells:promenade/spawn", proc))
    // Overground decorations
    add(Presets.indexed("minecells:promenade/chain_pile", 1, 2, 2, 2, terrainMatching = true))
    add(Presets.indexed("minecells:promenade/gallows", 1, 2, 2, 2, processors = proc))
    add(Presets.single("minecells:promenade/king_statue", proc, true))
    // Overground buildings
    add(Presets.prefixed("minecells:promenade/overground_buildings", "main/0" to 3, "main/1" to 1, "side/0" to 8, "side/1" to 6, processors = proc))
    add(Presets.indexed("minecells:promenade/overground_buildings/pit", 1, processors = proc))
    // Underground buildings
    add(Presets.indexed("minecells:promenade/underground_buildings/entry", 1, processors = proc))
    add(Presets.indexed("minecells:promenade/underground_buildings/shaft", 1, processors = proc))
    add(Presets.indexed("minecells:promenade/underground_buildings/shaft_bottom", 1, processors = proc))
    add(Presets.indexed("minecells:promenade/underground_buildings/room", 3, 2, 2, 1, 2, processors = proc))
    add(Presets.indexed("minecells:promenade/underground_buildings/chain_top", 1, processors = proc))
    add(Presets.indexed("minecells:promenade/underground_buildings/chain_bottom", 1, processors = proc))
    add(Presets.single("minecells:promenade/underground_buildings/room_end_center", proc))
    add(Presets.single("minecells:promenade/underground_buildings/room_end_left", proc))
    add(Presets.single("minecells:promenade/underground_buildings/room_end_right", proc))
    add(Presets.single("minecells:promenade/underground_buildings/end", proc))
    // Doorways
    add(Presets.indexed("minecells:promenade/doorway", 2, 1, 1, 1, 1, processors = proc))
    add(Presets.prefixed("minecells:promenade/doorway_decoration", "banners" to 1, "torches" to 2, "minecraft:empty" to 4, processors = proc))
    // Walls
    add(Presets.single("minecells:promenade/border_wall/underground", processors = proc))
    add(Presets.single("minecells:promenade/border_wall/bottom", processors = proc))
    add(Presets.single("minecells:promenade/border_wall/middle", processors = proc))
    add(Presets.single("minecells:promenade/border_wall/top", processors = proc))
    add(Presets.indexed("minecells:promenade/wall_segment", 3, 2, 2, 1, 1, 1, processors = proc))
  }

  fun insufferableCrypt() = Preset {
    val processors = "minecells:brick_decay"
    add(Presets.single("minecells:insufferable_crypt/spawn", processors))
    add(Presets.single("minecells:insufferable_crypt/elevator_shaft", processors))
    add(Presets.single("minecells:insufferable_crypt/boss_room", processors))
  }

  fun ramparts() = Preset {
    val processors = "minecells:promenade"

    add(Presets.single("minecells:ramparts/spawn", processors))
    add(Presets.single("minecells:ramparts/spawn_end", processors))
    add(Presets.single("minecells:ramparts/base", processors))
    add(Presets.single("minecells:ramparts/bottom", processors))
    add(Presets.single("minecells:ramparts/bottom_end", processors))
    add(Presets.single("minecells:ramparts/end", processors))
    add(Presets.indexed("minecells:ramparts/pole", 1, 1, 2, 1, processors = processors))
    add(Presets.indexed("minecells:ramparts/elevator_shaft", 4, 1, 4, 2, processors = processors))
    add(Presets.prefixed("minecells:ramparts/top",
      "flat" to 6,
      "floating_platforms" to 2,
      "room" to 3,
      "shooting_range" to 2,
      "two_sweepers" to 1,
      "wooden_over_spikes" to 2,
      "wooden_platform" to 3,
      processors = processors
    ))
  }

  fun blackBridge() = Preset {
    val processors = "minecells:promenade"

    listOf("bottom", "top").forEach { half ->
      for (i in 0..3) {
        add(Presets.single("minecells:black_bridge/$half$i", processors))
      }
    }
  }
}