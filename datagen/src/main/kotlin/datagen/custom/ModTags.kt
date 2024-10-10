package datagen.custom

import tada.lib.generator.ResourceGenerator
import tada.lib.tags.TagManager

fun ResourceGenerator.bowTags() {
  val bowsAcceptingInfinity = Constants.BOWS.filter {
    it != "bow_and_endless_quiver" && it != "quick_bow" && it != "ice_bow"
  }

  val bowsAcceptingFlame = Constants.BOWS.filter { it != "ice_bow" }

  fun transformToIds(list: List<String>) = list.map { "minecells:$it" }.toTypedArray()

  TagManager.add("minecells:items/bows/accepting_power", *transformToIds(Constants.BOWS))
  TagManager.add("minecells:items/bows/accepting_punch", *transformToIds(Constants.BOWS))
  TagManager.add("minecells:items/bows/accepting_infinity", *transformToIds(bowsAcceptingInfinity))
  TagManager.add("minecells:items/bows/accepting_flame", *transformToIds(bowsAcceptingFlame))
}