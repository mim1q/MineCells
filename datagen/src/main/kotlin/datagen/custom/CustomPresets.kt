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

  fun grassBlock(id: String, stoneId: String) = Preset {
    val (ns, name) = Id(id)
    val (sNs, sName) = Id(stoneId)
    add(name, ParentedModel.block("minecraft:block/grass_block")
      .texture("particle", "$sNs:block/$sName")
      .texture("bottom", "$sNs:block/$sName")
      .texture("side", "$sNs:block/$sName")
      .texture("top", "minecraft:block/grass_block_top")
      .texture("overlay", "$ns:block/${name}_overlay"))
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
    add("reset_rune", CraftingRecipe.shapeless("minecells:reset_rune", 1) {
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
    add("prison_doorway", CraftingRecipe.shaped("minecells:prison_doorway") {
      pattern("BBB")
      pattern("BIB")
      pattern("BTB")
      key("B", "minecells:prison_bricks")
      key("I", "minecraft:iron_bars")
      key("T", "minecells:prison_torch")
    })
  }
}