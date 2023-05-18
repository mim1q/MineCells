package datagen

import datagen.custom.CustomTemplatePoolPresets
import tada.lib.generator.ResourceGenerator
import tada.lib.presets.blocksets.BlockSets
import java.nio.file.Path

fun main(args: Array<String>) {
  println("Hello from datagen!")
  if (args.isEmpty()) throw IllegalArgumentException("Must provide an output directory")
  ResourceGenerator.create("minecells", Path.of(args[0])).apply {
    // Wood
    add(BlockSets.basicWoodSet("minecells:putrid"))
    // Template Pools
    add(CustomTemplatePoolPresets.common())
    add(CustomTemplatePoolPresets.prisonersQuarters())
    add(CustomTemplatePoolPresets.promenadeOfTheCondemned())
    add(CustomTemplatePoolPresets.insufferableCrypt())
  }.generate()
}