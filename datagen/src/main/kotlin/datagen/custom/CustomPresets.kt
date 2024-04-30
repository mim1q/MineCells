package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.common.CommonDropPresets
import tada.lib.presets.common.CommonModelPresets
import tada.lib.presets.common.CommonRecipePresets
import tada.lib.resources.blockstate.BlockState
import tada.lib.resources.blockstate.BlockStateModel
import tada.lib.resources.blockstate.BlockStateModel.Rotation
import tada.lib.resources.model.ParentedModel
import tada.lib.resources.recipe.CraftingRecipe
import tada.lib.resources.recipe.SmeltingRecipe
import tada.lib.resources.recipe.StonecuttingRecipe
import tada.lib.tags.TagManager
import tada.lib.util.Id

object CustomPresets {
  fun torch(id: String, texture: String) = Preset {
    val (ns, name) = Id(id)
    add("${name}_torch", ParentedModel.block("minecells:block/template/colored_torch").texture("flame", Id(texture).toString()))
    add("${name}_torch_standing", ParentedModel.block("minecells:block/template/colored_torch_standing").texture("flame", Id(texture).toString()))
    add("${name}_torch", BlockState.create {
      variant("facing=north, standing=false", BlockStateModel("$ns:block/${name}_torch", yRot = Rotation.NONE))
      variant("facing=east, standing=false", BlockStateModel("$ns:block/${name}_torch", yRot = Rotation.CW_90))
      variant("facing=south, standing=false", BlockStateModel("$ns:block/${name}_torch", yRot = Rotation.CW_180))
      variant("facing=west, standing=false", BlockStateModel("$ns:block/${name}_torch", yRot = Rotation.CW_270))
      variant("standing=true", BlockStateModel("$ns:block/${name}_torch_standing", yRot = Rotation.NONE))
    })
    add("${name}_torch", ParentedModel.item("minecells:block/template/colored_torch_inventory").texture("flame", Id(texture).toString()))
    add(CommonDropPresets.simpleDrop("$ns:${name}_torch"))
  }

  fun corpse(id: String, models: Boolean = false) = Preset {
    val (ns, name) = Id(id)
    listOf("hanged_", "").forEach {
      if (models) {
        add("$it$name", ParentedModel.block("minecells:block/${it}corpse").texture("0", "$ns:block/$name"))
      }
      add(CommonModelPresets.horizontallyRotateableBlock("$ns:$it$name"))
    }
  }

  fun grassBlock(id: String, stoneId: String, sideId: String = stoneId, overlayId: String = id) = Preset {
    val (ns, name) = Id(id)
    val (sNs, sName) = Id(stoneId)
    val (sdNs, sdName) = Id(sideId)
    val (oNs, oName) = Id(overlayId)
    add(name, ParentedModel.block("minecraft:block/grass_block")
      .texture("particle", "$sNs:block/$sName")
      .texture("bottom", "$sNs:block/$sName")
      .texture("side", "$sdNs:block/$sdName")
      .texture("top", "minecraft:block/grass_block_top")
      .texture("overlay", "$oNs:block/${oName}_overlay"))
    add(name, BlockState.createSingle("$ns:block/$name"))
    add(CommonModelPresets.itemBlockModel(id))
    add(CommonDropPresets.silkTouchDrop(id, stoneId, id))
  }

  fun doorway(id: String) = Preset {
    val (ns, name) = Id(id)
    add("${name}_doorway", ParentedModel.block("minecells:block/template/doorway").texture("0", "$ns:block/doorway/$name"))
    add("${name}_doorway", BlockState.create {
      variant("facing=north", BlockStateModel("$ns:block/${name}_doorway", yRot = Rotation.NONE))
      variant("facing=east", BlockStateModel("$ns:block/${name}_doorway", yRot = Rotation.CW_90))
      variant("facing=south", BlockStateModel("$ns:block/${name}_doorway", yRot = Rotation.CW_180))
      variant("facing=west", BlockStateModel("$ns:block/${name}_doorway", yRot = Rotation.CW_270))
    })
    if (name != "overworld") {
      add(CommonDropPresets.simpleDrop("${id}_doorway"))
    }
  }

  fun flag(id: String) = Preset {
    val (_, name) = Id(id)
    add(name, BlockState.createSingle("minecraft:block/white_wool"))
    add(name, ParentedModel.item("minecraft:builtin/entity").postProcess { addProperty("gui_light", "front") })
    add(CommonDropPresets.simpleDrop(id))
    TagManager.add("minecraft:mineable/axe", id)
  }

  fun customRecipes() = Preset {
    add(CommonRecipePresets.packed3x3("minecells:putrid_planks", "minecells:putrid_board_block", 9))
    add("putrid_boards", CraftingRecipe.shaped("minecells:putrid_boards", 12) {
      pattern("XXX")
      pattern("XXX")
      key("X", "minecells:putrid_board_block")
    })
    add("reset_rune", CraftingRecipe.shapeless("minecells:reset_rune") {
      ingredient("minecells:blank_rune")
      ingredient("minecraft:clock")
      ingredient("minecraft:emerald")
      ingredient("minecraft:emerald")
      ingredient("minecraft:emerald")
      ingredient("minecraft:emerald")
      ingredient("minecells:monsters_eye")
      ingredient("minecells:monsters_eye")
      ingredient("minecells:monsters_eye")
    })
    add("concierge_respawn_rune", CraftingRecipe.shapeless("minecells:concierge_respawn_rune") {
      ingredient("minecraft:flint")
      ingredient("minecraft:flint_and_steel")
      ingredient("minecraft:flint")
      ingredient("minecells:monsters_eye")
      ingredient("minecells:blank_rune")
      ingredient("minecells:guts")
      ingredient("minecraft:iron_ingot")
      ingredient("minecraft:iron_ingot")
      ingredient("minecraft:iron_ingot")
    })
    add("prison_doorway", CraftingRecipe.shaped("minecells:prison_doorway") {
      pattern("BBB")
      pattern("BIB")
      pattern("BTB")
      key("B", "minecells:prison_bricks")
      key("I", "minecraft:iron_bars")
      key("T", "minecells:prison_torch")
    })
    add("flag_pole", CraftingRecipe.shaped("minecells:flag_pole") {
      pattern("PPP")
      pattern(" S ")
      pattern("S  ")
      key("P", "minecells:putrid_planks")
      key("S", "minecraft:stick")
    })
    add("cracked_prison_bricks", SmeltingRecipe.create("minecells:prison_bricks", "minecells:cracked_prison_bricks"))
    add(dimensionalRuneRecipes())
    add("arrow_sign", CraftingRecipe.shaped("minecells:arrow_sign", 4) {
      pattern("PPS")
      pattern("PPS")
      key("P", "minecells:putrid_planks")
      key("S", "minecells:putrid_stairs")
    })
    // Bloomrock
    add(CommonRecipePresets.packed2x2("minecells:bloomrock", "minecells:bloomrock_bricks", 4))
    add(CommonRecipePresets.packed2x2("minecells:bloomrock_bricks", "minecells:bloomrock_tiles", 4))
    add("bloomrock_bricks_stonecutting", StonecuttingRecipe.create("minecells:bloomrock", "minecells:bloomrock_bricks", 1))
    add("bloomrock_tiles_stonecutting", StonecuttingRecipe.create("minecells:bloomrock_bricks", "minecells:bloomrock_tiles", 1))
    add("bloomrock_tiles_from_bricks_stonecutting", StonecuttingRecipe.create("minecells:bloomrock", "minecells:bloomrock_tiles", 1))
    add("cracked_bloomrock_bricks", SmeltingRecipe.create("minecells:bloomrock_bricks", "minecells:cracked_bloomrock_bricks"))
    // Chains
    add("chain_pile", CraftingRecipe.shaped("minecells:chain_pile", 4) {
      pattern(" C ")
      pattern("CCC")
      key("C", "minecells:big_chain")
    })
    add(CommonRecipePresets.packed2x2("minecells:big_chain", "minecells:chain_pile_block", 2))
    add("cooked_sewer_calamari", SmeltingRecipe.create("minecells:sewer_calamari", "minecells:cooked_sewer_calamari", 0.35))
    add("cooked_sewer_calamari_smoking", SmeltingRecipe.smoking("minecells:sewer_calamari", "minecells:cooked_sewer_calamari", 0.35))
  }

  private fun dimensionalRuneRecipes() = Preset {
    fun add(dimension: String, parent: String?, vararg ingredients: String) = add(
      "${dimension}_dimensional_rune",
      CraftingRecipe.shapeless("minecells:${dimension}_dimensional_rune", 1) {
        ingredient(parent?.let { "minecells:${parent}_dimensional_rune" } ?: "minecells:blank_rune")
        ingredients.forEach { ingredient(it) }
      }
    )
    add("prison", null, "minecells:prison_torch")
    add("promenade", "prison", "minecells:promenade_torch")
    add("insufferable_crypt", "prison", "minecells:monsters_eye")
    add("ramparts", "prison", "minecells:ramparts_torch")
    add("black_bridge", "ramparts", "minecraft:flint")
  }
}