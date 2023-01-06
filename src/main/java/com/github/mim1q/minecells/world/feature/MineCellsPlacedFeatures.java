package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.ArrayList;
import java.util.List;

public class MineCellsPlacedFeatures {
  public static final RegistryEntry<PlacedFeature> PROMENADE_TREE = createPlacedFeature(
    MineCells.createId("promenade_tree"),
    MineCellsConfiguredFeatures.PROMENADE_TREE,
    PlacedFeatures.createCountExtraModifier(1, 0.5F, 3),
    SquarePlacementModifier.of(),
    PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
    BlockFilterPlacementModifier.of(BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.DIRT))
  );

  public static final RegistryEntry<PlacedFeature> BIG_PROMENADE_TREE = createPlacedFeature(
    MineCells.createId("big_promenade_tree"),
    MineCellsConfiguredFeatures.BIG_PROMENADE_TREE,
    PlacedFeatures.createCountExtraModifier(0, 0.25F, 1),
    SquarePlacementModifier.of(),
    PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
    BlockFilterPlacementModifier.of(BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.DIRT))
  );

  public static <FC extends FeatureConfig> RegistryEntry<PlacedFeature> createPlacedFeature(Identifier id, RegistryEntry<ConfiguredFeature<FC, ?>> feature, PlacementModifier... placementModifiers) {
    List<PlacementModifier> list = new ArrayList<>(List.of(placementModifiers));
    return createPlacedFeature(id, feature, list);
  }

  public static <FC extends FeatureConfig> RegistryEntry<PlacedFeature> createPlacedFeature(Identifier id, RegistryEntry<ConfiguredFeature<FC, ?>> feature, List<PlacementModifier> placementModifiers) {
    return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, id, new PlacedFeature(RegistryEntry.upcast(feature), List.copyOf(placementModifiers)));
  }
}
