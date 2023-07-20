package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.blocksets.BlockSets
import tada.lib.presets.common.CommonDropPresets
import tada.lib.presets.common.CommonModelPresets
import tada.lib.presets.common.CommonRecipePresets
import tada.lib.resources.blockstate.BlockState
import tada.lib.resources.blockstate.BlockStateModel
import tada.lib.resources.blockstate.BlockStateModel.Rotation
import tada.lib.resources.model.ParentedModel
import tada.lib.resources.recipe.SmeltingRecipe
import tada.lib.resources.recipe.StonecuttingRecipe
import tada.lib.tags.TagManager
import tada.lib.util.Id

object CustomBlockSets {
  fun stoneFamily(id: String) = Preset {
    val (ns, name) = Id(id)
    add(BlockSets.fullStoneSet("${ns}:${name}_stone", false))
    add(CommonDropPresets.silkTouchDrop("${ns}:${name}_stone", "${ns}:${name}_cobblestone", "${ns}:${name}_stone"))
    TagManager.add("items/stone_tool_materials", "${ns}:${name}_cobblestone")
    add(BlockSets.basicStoneSet("${ns}:${name}_cobblestone", true))
    add("${name}_cobblestone_to_${name}_stone", SmeltingRecipe.create("${ns}:${name}_cobblestone", "${ns}:${name}_stone"))
    add(BlockSets.basicStoneSet("${ns}:${name}_brick", true, "s"))
    add(CommonRecipePresets.packed2x2("${ns}:${name}_stone", "${ns}:${name}_bricks", 4))
    add("${name}_stone_to_${name}_bricks_stonecutting", StonecuttingRecipe.create("${ns}:${name}_stone", "${ns}:${name}_bricks"))
    add("${name}_stone_to_small_${name}_bricks_stonecutting", StonecuttingRecipe.create("${ns}:${name}_stone", "${ns}:small_${name}_bricks"))
    add("${name}_bricks_to_small_${name}_bricks_stonecutting", StonecuttingRecipe.create("${ns}:${name}_bricks", "${ns}:small_${name}_bricks"))
    add(BlockSets.basicStoneSet("${ns}:small_${name}_brick", true, "s"))
    add(CommonRecipePresets.packed2x2("${ns}:${name}_bricks", "${ns}:small_${name}_bricks", 4))
  }

  fun leaves(id: String, saplingId: String? = null) = Preset {
    val (ns, name) = Id(id)
    add("${name}_leaves", ParentedModel.block("minecraft:block/leaves").texture("all", "${ns}:block/${name}_leaves"))
    add("${name}_hanging_leaves", ParentedModel.block("minecells:block/hanging_leaves").texture("0", "${ns}:block/${name}_hanging_leaves"))
    add("${name}_wall_leaves", ParentedModel.block("minecells:block/wall_leaves")
      .texture("base", "${ns}:block/${name}_wall_leaves")
      .texture("detail", "${ns}:block/${name}_wall_leaves_detail")
    )
    if (saplingId != null) {
      val (sNs, sName) = Id(saplingId)
      add(sName, ParentedModel.block("minecraft:block/cross").texture("cross", "$sNs:block/$sName"))
      add(sName, BlockState.createSingle("$sNs:block/$sName"))
      TagManager.add("blocks/saplings", "$sNs:$sName")
      TagManager.add("items/saplings", "$sNs:$sName")
    }
    listOf("leaves", "hanging_leaves", "wall_leaves").forEach {
      add(CommonModelPresets.itemBlockModel("$ns:${name}_$it"))
      add(CommonDropPresets.leavesDrop("$ns:${name}_$it", saplingId))
    }
    add("${name}_leaves", BlockState.createSingle("$ns:block/${name}_leaves"))
    add("${name}_hanging_leaves", BlockState.create {
      variant("facing=north", BlockStateModel("$ns:block/${name}_hanging_leaves", yRot = Rotation.NONE))
      variant("facing=east", BlockStateModel("$ns:block/${name}_hanging_leaves", yRot = Rotation.CW_90))
      variant("facing=south", BlockStateModel("$ns:block/${name}_hanging_leaves", yRot = Rotation.CW_180))
      variant("facing=west", BlockStateModel("$ns:block/${name}_hanging_leaves", yRot = Rotation.CW_270))
    })
    add("${name}_wall_leaves", BlockState.create {
      variant("facing=north", BlockStateModel("$ns:block/${name}_wall_leaves", yRot = Rotation.NONE))
      variant("facing=east", BlockStateModel("$ns:block/${name}_wall_leaves", yRot = Rotation.CW_90))
      variant("facing=south", BlockStateModel("$ns:block/${name}_wall_leaves", yRot = Rotation.CW_180))
      variant("facing=west", BlockStateModel("$ns:block/${name}_wall_leaves", yRot = Rotation.CW_270))
      variant("facing=down", BlockStateModel("$ns:block/${name}_wall_leaves", xRot = Rotation.CW_90, yRot = Rotation.CW_90))
      variant("facing=up", BlockStateModel("$ns:block/${name}_wall_leaves", xRot = Rotation.CW_270, yRot = Rotation.CW_90))
    })

    TagManager.add("blocks/leaves", "$ns:${name}_leaves")
    TagManager.add("items/leaves", "$ns:${name}_leaves")
  }
}