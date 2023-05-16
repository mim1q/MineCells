package datagen

import datagen.custom.CustomTemplatePoolPresets
import tada.lib.generator.ResourceGenerator
import java.nio.file.Path

fun main(args: Array<String>) {
  println("Hello from datagen!")
  if (args.isEmpty()) throw IllegalArgumentException("Must provide an output directory")
  ResourceGenerator.create("minecells", Path.of(args[0])).apply {
    // Template Pools
    add(CustomTemplatePoolPresets.prisonersQuarters())
  }.generate()
}