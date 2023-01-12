package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

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

  public static final RegistryEntry<PlacedFeature> PROMENADE_SHRUB = createPlacedFeature(
    MineCells.createId("promenade_shrub"),
    MineCellsConfiguredFeatures.PROMENADE_SHRUB,
    PlacedFeatures.createCountExtraModifier(0, 0.5F, 4),
    SquarePlacementModifier.of(),
    PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
    BlockFilterPlacementModifier.of(BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.DIRT))
  );

  public static final RegistryEntry<PlacedFeature> PROMENADE_CHAINS = createJigsawFeature("promenade_chains", MineCellsConfiguredFeatures.PROMENADE_CHAINS, 2);
  public static final RegistryEntry<PlacedFeature> PROMENADE_GALLOWS = createJigsawFeature("promenade_gallows", MineCellsConfiguredFeatures.PROMENADE_GALLOWS, 4);
  public static final RegistryEntry<PlacedFeature> PROMENADE_KING_STATUE = createJigsawFeature("promenade_king_statue", MineCellsConfiguredFeatures.PROMENADE_KING_STATUE, 20);

  public static <FC extends FeatureConfig> RegistryEntry<PlacedFeature> createPlacedFeature(Identifier id, RegistryEntry<ConfiguredFeature<FC, ?>> feature, PlacementModifier... placementModifiers) {
    List<PlacementModifier> list = new ArrayList<>(List.of(placementModifiers));
    return createPlacedFeature(id, feature, list);
  }

  public static <FC extends FeatureConfig> RegistryEntry<PlacedFeature> createPlacedFeature(Identifier id, RegistryEntry<ConfiguredFeature<FC, ?>> feature, List<PlacementModifier> placementModifiers) {
    return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, id, new PlacedFeature(RegistryEntry.upcast(feature), List.copyOf(placementModifiers)));
  }

  private static <FC extends FeatureConfig> RegistryEntry<PlacedFeature> createJigsawFeature(String id, RegistryEntry<ConfiguredFeature<FC, ?>> feature, int rarity, PlacementModifier... additionalModifiers) {
    List<PlacementModifier> modifiers = new ArrayList<>();
    modifiers.add(SquarePlacementModifier.of());
    modifiers.add(RarityFilterPlacementModifier.of(rarity));
    modifiers.add(HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING));
    modifiers.add(BlockFilterPlacementModifier.of(BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.DIRT)));
    modifiers.addAll(List.of(additionalModifiers));
    return createPlacedFeature(MineCells.createId(id), feature, modifiers);
  }
}
