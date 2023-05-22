package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.blocksets.BlockSets
import tada.lib.presets.common.CommonDropPresets
import tada.lib.presets.common.CommonRecipePresets
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
}