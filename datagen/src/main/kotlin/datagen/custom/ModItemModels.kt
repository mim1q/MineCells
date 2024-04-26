package datagen.custom

import com.google.gson.JsonArray
import tada.lib.presets.Preset
import tada.lib.presets.common.CommonModelPresets
import tada.lib.resources.model.ParentedModel
import tada.lib.util.json

object ModItemModels {
  fun generated() = Preset {
    listOf(
      "cage", "broken_cage", "blank_rune", "conjunctivius_respawn_rune", "vine_rune", "guts", "monsters_eye", "sewage_bucket",
      "ancient_sewage_bucket", "health_flask", "king_statue", "barrier_rune", "elevator_mechanism",
      "reset_rune", "concierge_respawn_rune", "monster_cell", "boss_stem_cell", "arrow_sign", "guidebook",
      "electric_whip", "throwing_knife", "firebrands", "explosive_bolt", "ice_arrow", "explosive_bulb", "infected_flesh",
      "cell_infused_steel", "metal_shards", "buzzcutter_fang", "molten_chunk", "sewer_calamari", "cooked_sewer_calamari",
      "transposition_core", "blood_bottle", "arcane_goo"
    ).forEach {
      if (it.startsWith("block/")) {
        add(CommonModelPresets.generatedItemModel("minecells:${it.removePrefix("block/")}", "block"))
      } else {
        add(CommonModelPresets.generatedItemModel("minecells:$it"))
      }
    }
    add("solid_barrier_rune", ParentedModel.item("minecells:item/barrier_rune"))
    add("unbreakable_chain", ParentedModel.item("minecraft:item/chain"))
    add("weapon/phaser", ParentedModel.item("minecraft:item/generated").texture("layer0", "minecells:item/phaser"))
  }

  fun handheld() = Preset {
    listOf("assassins_dagger", "cursed_sword", "tentacle").forEach {
      add("weapon/$it", ParentedModel.item("minecraft:item/handheld") {
        texture("layer0", "minecells:item/$it")
      })
    }
  }

  fun spawnEggs() = Preset {
    listOf(
      "leaping_zombie", "shocker", "grenadier", "disgusting_worm", "inquisitor", "kamikaze", "protector", "undead_archer",
      "shieldbearer", "mutated_bat", "sewers_tentacle", "rancid_rat", "runner", "scorpion", "buzzcutter", "sweeper"
    ).forEach {
      add(
        "${it}_spawn_egg",
        ParentedModel.item("minecraft:item/generated")
          .texture("layer0", "minecells:item/spawn_eggs/$it")
      )
    }
  }

  fun blockModels() = Preset {
    listOf("brittle_barrel", "spikes", "spawner_rune").forEach {
      add(CommonModelPresets.itemBlockModel("minecells:$it"))
    }
    add("flag_pole", ParentedModel.item("minecells:block/flag_pole_connecting"))
  }

  fun dimensionalRunes() = Preset {
    Constants.MINECELLS_DIMENSIONS.forEach {
      add("${it}_dimensional_rune", ParentedModel.item("minecells:item/dimensional_rune"))
    }
  }

  fun bows() = Preset {
    Constants.BOWS.forEach {
      add(it, ParentedModel.item("minecells:item/base_bow") {
        texture("layer0", "minecells:item/bow/$it")
      }.postProcess {
        val overrides = JsonArray().apply {
          add(json {
            "predicate" { "minecells:pulling" to 1 }
            "model" to "minecells:item/${it}_pulling_0"
          })
          add(json {
            "predicate" { "minecells:pulling" to 1; "minecells:pull" to 0.5 }
            "model" to "minecells:item/${it}_pulling_1"
          })
          add(json {
            "predicate" { "minecells:pulling" to 1; "minecells:pull" to 1.0 }
            "model" to "minecells:item/${it}_pulling_2"
          })
        }
        add("overrides", overrides)
      })

      for (i in 0 until 3) {
        add("${it}_pulling_$i", ParentedModel.item("minecells:item/base_bow") {
          texture("layer0", "minecells:item/bow/${it}_pulling_$i")
        })
      }
    }
  }

  fun crossbows() = Preset {
    Constants.CROSSBOWS.forEach {
      add(it, ParentedModel.item("minecells:item/base_crossbow") {
        texture("layer0", "minecells:item/bow/$it")
      }.postProcess {
        val overrides = JsonArray().apply {
          add(json {
            "predicate" { "minecells:pulling" to 1 }
            "model" to "minecells:item/${it}_pulling_0"
          })
          add(json {
            "predicate" { "minecells:pulling" to 1; "minecells:pull" to 0.5 }
            "model" to "minecells:item/${it}_pulling_1"
          })
          add(json {
            "predicate" { "minecells:pulling" to 1; "minecells:pull" to 1.0 }
            "model" to "minecells:item/${it}_pulling_2"
          })
          add(json {
            "predicate" { "minecells:charged" to 1 }
            "model" to "minecells:item/${it}_charged"
          })
        }
        add("overrides", overrides)
      })

      for (i in 0 until 3) {
        add("${it}_pulling_$i", ParentedModel.item("minecells:item/base_crossbow") {
          texture("layer0", "minecells:item/bow/${it}_pulling_$i")
        })
      }
      add("${it}_charged", ParentedModel.item("minecells:item/base_crossbow_charged") {
        texture("layer0", "minecells:item/bow/${it}_pulling_2")
      })
    }
  }

  fun shields() = Preset {
    Constants.SHIELDS.forEach {
      add(it, ParentedModel.item("minecraft:item/generated").texture("layer0", "minecells:item/shield/$it"))
    }
  }

  fun weaponCopies() = Preset {
    Constants.MELEE_AND_SKILLS.forEach {
      add(it, ParentedModel.item("minecells:item/weapon/$it"))
    }
    add("lightning_bolt", ParentedModel.item("minecells:item/weapon/lightning_bolt"))
  }
}