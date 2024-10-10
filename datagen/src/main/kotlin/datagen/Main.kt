package datagen

import datagen.custom.*
import datagen.custom.Constants.MINECELLS_DIMENSIONS
import tada.lib.generator.BeautifiedJsonFormatter
import tada.lib.generator.FilesystemFileSaver
import tada.lib.generator.ResourceGenerator
import tada.lib.lang.FlattenedJson
import tada.lib.lang.LanguageHelper
import tada.lib.presets.blocksets.BlockSets
import tada.lib.presets.common.CommonDropPresets
import tada.lib.presets.common.CommonModelPresets
import tada.lib.resources.blockstate.BlockState
import tada.lib.resources.model.ParentedModel
import tada.lib.tags.TagManager
import java.nio.file.Path

fun main(args: Array<String>) {
  if (args.size != 4) {
    throw IllegalArgumentException("Must provide an output directory, src directory, helper lang directory, and en_us lang map.")
  }
  val path = Path.of(args[0]).toAbsolutePath()
  val helperLangPath = Path.of(args[2]).toAbsolutePath()
  println("Running datagen script."
    + "\n  Output directory: $path"
    + "\n  Generated language helper directory: $helperLangPath"
  )

  val excludedFiles = arrayOf(
    ".*/assets/minecells/models/block/putrid_fence_(side|post).json",
    ".*/assets/minecells/blockstates/putrid_fence.json",
    ".*/assets/minecells/models/item/putrid_fence.json",
  )

  val generator = ResourceGenerator(
    "minecells",
    path,
    SelectiveFileSaver(*excludedFiles),
    BeautifiedJsonFormatter
  ).apply {
    // Wood
    add(BlockSets.basicWoodSet("minecells:putrid"))
    add(CustomBlockSets.leaves("minecells:wilted", "minecells:putrid_sapling"))
    add(CustomBlockSets.leaves("minecells:orange_wilted", "minecells:orange_putrid_sapling"))
    add(CustomBlockSets.leaves("minecells:red_wilted", "minecells:red_putrid_sapling"))
    // Stone
    add(CustomBlockSets.stoneFamily("minecells:prison"))
    add(BlockSets.basicStoneSet("minecells:cracked_prison_brick", baseSuffix = "s"))
    add(BlockSets.basicStoneSet("minecells:bloomrock"))
    add(BlockSets.basicStoneSet("minecells:bloomrock_tile", baseSuffix = "s"))
    add(BlockSets.basicStoneSet("minecells:bloomrock_brick", baseSuffix = "s"))
    add(BlockSets.basicStoneSet("minecells:cracked_bloomrock_brick", baseSuffix = "s"))
    // Torches
    val torches = listOf("prison", "promenade", "ramparts")
    torches.forEach { add(CustomPresets.torch("minecells:$it", "minecells:block/colored_torch/$it")) }
    // Other
    add(CommonModelPresets.cubeAllBlock("minecells:kingdom_portal_core"))
    add(BlockSets.basicSet("minecells:putrid_board", "_block"))
    add(CustomPresets.grassBlock("minecells:wilted_grass_block", "minecells:prison_stone", "minecells:wilted_grass_block"))
    add(CustomPresets.grassBlock("minecells:bloomrock_wilted_grass_block", "minecells:bloomrock", "minecells:bloomrock_wilted_grass_block", "minecells:wilted_grass_block"))
    add(CustomPresets.corpse("minecells:corpse"))
    add(CustomPresets.corpse("minecells:rotting_corpse", true))
    add(CustomPresets.corpse("minecells:skeleton"))
    listOf("elevator_assembler", "hardstone", "chain_pile_block", "runic_vine_stone").forEach {
      add(CommonModelPresets.cubeAllBlock("minecells:$it"))
    }
    add(CommonModelPresets.pillarBlock("minecells:crate"))
    add("invisible_stone", ParentedModel.block("minecraft:block/air").texture("particle", "minecells:block/prison_stone"))
    listOf("conjunctivius_box", "concierge_box", "beam_placer", "doorway_frame", "unbreakable_doorway_frame",
      "solid_barrier_rune", "conditional_barrier", "boss_barrier_controller", "boss_entry_barrier_controller",
      "player_barrier_controller", "spawner_rune", "rift", "arrow_sign"
    ).forEach {
      add(it, BlockState.createSingle("minecells:block/invisible_stone"))
    }
    listOf("overworld", "prison", "promenade", "insufferable_crypt", "ramparts", "black_bridge").forEach {
      add(CustomPresets.doorway("minecells:$it"))
      add("${it}_doorway", ParentedModel.item("minecells:item/doorway"))
    }
    add("runic_vine_plant", BlockState.createSingle("minecells:block/runic_vine_plant"))
    listOf("runic_vine", "runic_vine_top").forEach {
      add(it, ParentedModel.block("minecraft:block/cross").texture("cross", "minecells:block/$it"))
    }
    add("arrow_sign", BlockState.createSingle("minecells:block/putrid_planks"))
    add(CommonModelPresets.horizontallyRotateableBlock("minecells:cell_crafter"))
    add(CommonModelPresets.horizontallyRotateableBlock("minecells:unbreakable_cell_crafter", "minecells:cell_crafter"))
    // Flags
    listOf(
      "kings_crest", "torn_kings_crest", "promenade_of_the_condemned", "ramparts", "black_bridge", "insufferable_crypt",
      "large_red_ribbon", "red_ribbon"
    ).forEach {
      add(CustomPresets.flag("minecells:${it}_flag"))
    }

    add(CommonModelPresets.horizontallyRotateableBlock("minecells:return_stone"))
    add(CustomPresets.customRecipes())
    // Fluids
    listOf("sewage", "ancient_sewage").forEach {
      add(it, BlockState.createSingle("minecraft:block/water"))
    }
    // Item Models
    add(ModItemModels.generated())
    add(ModItemModels.handheld())
    add(ModItemModels.spawnEggs())
    add(ModItemModels.blockModels())
    add(ModItemModels.dimensionalRunes())
    add(ModItemModels.bows())
    add(ModItemModels.crossbows())
    add(ModItemModels.shields())
    add("weapon/flint", ParentedModel.item("minecells:item/weapon/balanced_blade").texture("layer0", "minecells:item/flint"))
    add(ModItemModels.weaponCopies())
    // Block drops
    listOf(
      "elevator_assembler", "chain_pile_block", "putrid_boards", "crate", "small_crate", "brittle_barrel", "flag_pole",
      "big_chain", "broken_cage", "prison_doorway", "king_statue", "chain_pile",
      "putrid_board_block", "arrow_sign", "cell_crafter"
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
    add(ModTemplatePools.ramparts())
    add(ModTemplatePools.blackBridge())
    // Tags
    TagManager.add("blocks/mineable/pickaxe",
      "minecells:big_chain", "minecells:chain_pile", "minecells:chain_pile_block", "minecells:cage",
      "minecells:broken_cage", "minecells:doorway_frame", "minecells:king_statue", "minecells:wilted_grass_block",
      "minecells:bloomrock_wilted_grass_block"
    )
    TagManager.add("blocks/mineable/axe",
      "minecells:flag_pole", "minecells:putrid_boards", "minecells:elevator_assembler", "minecells:crate",
      "minecells:small_crate", "minecells:brittle_barrel", "minecells:putrid_board_block", "minecells:putrid_board_slab",
      "minecells:putrid_board_stairs", "minecells:arrow_sign"
    )
    TagManager.add("minecells:blocks/tree_root_replaceable",
      "minecraft:air", "minecells:prison_stone", "minecells:prison_cobblestone", "minecells:wilted_grass_block",
      "minecells:bloomrock_wilted_grass_block"
    )
    TagManager.add("minecells:items/discard_in_high_dimensions",
      "minecraft:stick", "minecells:red_putrid_sapling"
    )
    bowTags()
    // Loot Tables for Advancements
    MINECELLS_DIMENSIONS.forEach {
      add(CustomHardcodedPresets.advancementDrop(it, "minecells:${it}_dimensional_rune"))
    }
    // Sounds
    mineCellsSounds()

    // Generated images
    CustomImages.createBowTextures("bow_atlas.png", Path.of(args[0]))
  }

  generator.generate()

  // Generate in the default resources dir instead of `generated`
  ResourceGenerator(
    "minecells",
    Path.of(args[1]),
    FilesystemFileSaver,
    BeautifiedJsonFormatter
  ).apply {
    // Lang
    val langFile = Path.of(args[3]).toFile()
    add("en_us", FlattenedJson(langFile, "lang", "assets"))
  }.generate()

  LanguageHelper.create(Path.of(args[1]).toAbsolutePath().resolve("assets/minecells/lang/"), Path.of(args[2]).toAbsolutePath()) {
    automaticallyGenerateBlockEntries(generator)
    generateMissingLangEntries()
  }
}


