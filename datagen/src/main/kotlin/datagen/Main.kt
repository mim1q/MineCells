package datagen

import datagen.custom.CustomBlockSets
import datagen.custom.ModItemModels
import datagen.custom.ModTemplatePools
import tada.lib.generator.ResourceGenerator
import tada.lib.presets.blocksets.BlockSets
import java.nio.file.Path

fun main(args: Array<String>) {
  println("Hello from datagen!")
  if (args.isEmpty()) throw IllegalArgumentException("Must provide an output directory")
  ResourceGenerator.create("minecells", Path.of(args[0])).apply {
    // Wood
    add(BlockSets.basicWoodSet("minecells:putrid"))
    add(CustomBlockSets.leaves("minecells:wilted"))
    add(CustomBlockSets.leaves("minecells:orange_wilted"))
    add(CustomBlockSets.leaves("minecells:red_wilted"))
    // Stone
    add(CustomBlockSets.stoneFamily("minecells:prison"))
    // Other
    add(BlockSets.basicSet("minecells:putrid_board", "_block"))
    // Item Models
    add(ModItemModels.generated())
    add(ModItemModels.handheld())
    add(ModItemModels.spawnEggs())
    // Template Pools
    add(ModTemplatePools.common())
    add(ModTemplatePools.prisonersQuarters())
    add(ModTemplatePools.promenadeOfTheCondemned())
    add(ModTemplatePools.insufferableCrypt())
  }.generate()
}





