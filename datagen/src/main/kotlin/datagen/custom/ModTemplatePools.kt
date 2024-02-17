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
    val processors = "minecells:promenade"
    // Spawn
    add(Presets.single("minecells:promenade/spawn", processors))
    // Overground decorations
    add(Presets.indexed("minecells:promenade/chain_pile", 1, 2, 2, 2, terrainMatching = true))
    add(Presets.indexed("minecells:promenade/gallows", 1, 2, 2, 2, processors = processors))
    add(Presets.single("minecells:promenade/king_statue", processors, true))
    // Buildings
    add(Presets.indexed("minecells:promenade/overground_end", 1, 2, 2, processors = processors))
    add(Presets.indexed("minecells:promenade/overground", 2, 1, 2, 2, 1, processors = processors))
    add(Presets.indexed("minecells:promenade/overground_top", 1, 2, 3, 1, 2, 2, 2, 2, processors = processors))
    add(Presets.indexed("minecells:promenade/underground", 3, 3, 2, 2, 3, 4, processors = processors))
    add(Presets.indexed("minecells:promenade/underground_end", 1, processors = processors))
    add(Presets.single("minecells:promenade/overground_elevator", processors = processors))
    add(Presets.single("minecells:promenade/overground_base", processors = processors))
    // Walls
    add(Presets.single("minecells:promenade/border_wall/underground", processors = processors))
    add(Presets.single("minecells:promenade/border_wall/bottom", processors = processors))
    add(Presets.single("minecells:promenade/border_wall/middle", processors = processors))
    add(Presets.single("minecells:promenade/border_wall/top", processors = processors))
    add(Presets.indexed("minecells:promenade/wall_segment", 28, 2, 2, 1, 1, 1, processors = processors))
    // Ramparts tower
    add(Presets.single("minecells:promenade/ramparts_tower", processors))
    // Paths
    val pathProc = "minecells:promenade/path"
    add(Presets.indexed("minecells:promenade/path/straight", 1, 1, 1, processors = pathProc, terrainMatching = true))
    add(Presets.indexed("minecells:promenade/path/turn", 1, 2, processors = pathProc, terrainMatching = true))
    add(Presets.single("minecells:promenade/path/half", processors = pathProc, terrainMatching = true))
    add(Presets.indexed("minecells:promenade/path/building", 1, processors = pathProc, terrainMatching = true))
    add(Presets.single("minecells:promenade/path/crossroads", processors = pathProc, terrainMatching = true))
    add(Presets.single("minecells:promenade/path/crossroads_post", processors = processors))
    // Special
    add(Presets.single("minecells:promenade/special/vine_rune", processors = processors))
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
    add(Presets.prefixed(
      "minecells:ramparts/pole",
      "0" to 1, "1" to 1, "2" to 2, "3" to 1, "minecraft:empty" to 4
    ))
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
    add(Presets.indexed("minecells:ramparts/top_entry", 1, 1, processors = processors))
    add(Presets.indexed("minecells:ramparts/room_entry", 1, 1, processors = processors))
    add(Presets.indexed("minecells:ramparts/room_exit", 1, processors = processors))
    add(Presets.indexed("minecells:ramparts/room_end", 1, processors = processors))
    add(Presets.prefixed("minecells:ramparts/secret_room",
      "0" to 1,
      "1" to 1,
      "empty" to 20,
      processors = processors
    ))
    add(Presets.prefixed("minecells:ramparts/room",
      "corridor" to 7,
      "stacked_corridor" to 5,
      "corridor_with_alcove" to 5,
      "stacked_corridor" to 5,
      "gated_corridor" to 3,
      "wooden_see_through" to 2,
      processors = processors
    ))

    // Towers
    add(Presets.indexed("minecells:ramparts/tower/room", 3, 3, 3, 1, 2, processors = processors))
    add(Presets.indexed("minecells:ramparts/tower/top", 1, 1, processors = processors))
    add(Presets.single("minecells:ramparts/tower/bottom", processors = processors))
    add(Presets.single("minecells:ramparts/tower/base", processors = processors))
    add(Presets.single("minecells:ramparts/end_tower/entrance", processors = processors))
    add(Presets.single("minecells:ramparts/end_tower/elevator_shaft", processors = processors))
    add(Presets.single("minecells:ramparts/end_tower/exit", processors = processors))
    add(Presets.prefixed("minecells:ramparts/tower/entry_room",
      "double" to 3,
      "double_bookshelves" to 2,
      "ranged_platforms" to 2,
      "minecells:ramparts/tower/room/3" to 1,
      processors = processors
    ))

    // Platforms
    add(Presets.prefixed("minecells:ramparts/platform", "bridge" to 1, "broken_bridge" to 1, "islands" to 1, processors = processors))
    add(Presets.prefixed("minecells:ramparts/platform_up", "bridge" to 1, "islands" to 1, processors = processors))
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