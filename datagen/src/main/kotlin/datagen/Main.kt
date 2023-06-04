package datagen

import datagen.custom.CustomBlockSets
import datagen.custom.CustomPresets
import datagen.custom.ModItemModels
import datagen.custom.ModTemplatePools
import tada.lib.generator.ResourceGenerator
import tada.lib.lang.LanguageHelper
import tada.lib.presets.blocksets.BlockSets
import tada.lib.presets.common.CommonDropPresets
import tada.lib.presets.common.CommonModelPresets
import tada.lib.resources.blockstate.BlockState
import tada.lib.resources.model.ParentedModel
import tada.lib.tags.TagManager
import java.nio.file.Path

fun main(args: Array<String>) {
  if (args.size != 3) {
    throw IllegalArgumentException("Must provide an output directory, lang directory and helper lang directory.")
  }
  val path = Path.of(args[0]).toAbsolutePath()
  val helperLangPath = Path.of(args[2]).toAbsolutePath()
  println(
    "Running datagen script." +
    "\n  Output directory: $path" +
    "\n  Generated language helper directory: $helperLangPath"
  )
  val generator = ResourceGenerator.create("minecells", path).apply {
    // Wood
    add(BlockSets.basicWoodSet("minecells:putrid"))
    add(CustomBlockSets.leaves("minecells:wilted"))
    add(CustomBlockSets.leaves("minecells:orange_wilted"))
    add(CustomBlockSets.leaves("minecells:red_wilted"))
    // Stone
    add(CustomBlockSets.stoneFamily("minecells:prison"))
    // Torches
    val torches = listOf("prison", "promenade")
    torches.forEach { add(CustomPresets.torch("minecells:$it", "minecells:block/colored_torch/$it")) }
    // Other
    add(BlockSets.basicSet("minecells:putrid_board", "_block"))
    add(CustomPresets.grassBlock("minecells:wilted_grass_block", "minecells:prison_stone"))
    add(CustomPresets.corpse("minecells:corpse"))
    add(CustomPresets.corpse("minecells:rotting_corpse", true))
    add(CustomPresets.corpse("minecells:skeleton"))
    listOf("elevator_assembler", "hardstone", "chain_pile_block", "runic_vine_stone").forEach {
      add(CommonModelPresets.cubeAllBlock("minecells:$it"))
    }
    add(CommonModelPresets.pillarBlock("minecells:crate"))
    listOf("conjunctivius_box", "beam_placer", "doorway_frame").forEach {
      add(it, BlockState.createSingle("minecraft:block/air"))
    }
    listOf("overworld", "prison", "promenade", "insufferable_crypt").forEach {
      add(CustomPresets.doorway("minecells:$it"))
    }
    add("runic_vine_plant", BlockState.createSingle("minecells:block/runic_vine_plant"))
    listOf("runic_vine", "runic_vine_top").forEach {
      add(it, ParentedModel.block("minecraft:block/cross").texture("cross", "minecells:block/$it"))
    }
    add(CommonModelPresets.horizontallyRotateableBlock("minecells:return_stone"))
    // Fluids
    listOf("sewage", "ancient_sewage").forEach {
      add(it, BlockState.createSingle("minecraft:block/water"))
    }
    // Item Models
    add(ModItemModels.generated())
    add(ModItemModels.handheld())
    add(ModItemModels.spawnEggs())
    add(ModItemModels.blockModels())
    // Block drops
    listOf(
      "elevator_assembler", "chain_pile_block", "putrid_boards", "crate", "small_crate", "brittle_barrel", "flag_pole",
      "big_chain", "broken_cage", "biome_banner"
    ).forEach {
      add(CommonDropPresets.simpleDrop("minecells:$it"))
    }
    listOf("alchemy_equipment_0", "alchemy_equipment_1", "alchemy_equipment_2").forEach {
      add(CommonDropPresets.silkTouchOnlyDrop("minecells:$it"))
    }
    // Template Pools
    add(ModTemplatePools.common())
    add(ModTemplatePools.prisonersQuarters())
    add(ModTemplatePools.promenadeOfTheCondemned())
    add(ModTemplatePools.insufferableCrypt())
    // Tags
    TagManager.add("blocks/mineable/pickaxe",
      "minecells:big_chain", "minecells:chain_pile", "minecells:chain_pile_block", "minecells:cage",
      "minecells:broken_cage"
    )
    TagManager.add("blocks/mineable/axe",
      "minecells:flag_pole", "minecells:putrid_boards", "minecells:elevator_assembler", "minecells:crate",
      "minecells:small_crate", "minecells:brittle_barrel", "minecells:biome_banner"
    )
  }
  generator.generate()

  LanguageHelper.create(Path.of(args[1]).toAbsolutePath(), Path.of(args[2]).toAbsolutePath()) {
    automaticallyGenerateBlockEntries(generator)
    generateMissingLangEntries()
  }
}





