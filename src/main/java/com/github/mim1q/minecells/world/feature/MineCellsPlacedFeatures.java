package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.MineCellsBlockTags;
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
import net.minecraft.world.gen.placementmodifier.*;

import java.util.ArrayList;
import java.util.List;

public class MineCellsPlacedFeatures {
  public static final RegistryEntry<PlacedFeature> PROMENADE_TREE = createPlacedFeature(
    MineCells.createId("promenade_tree"),
    MineCellsConfiguredFeatures.PROMENADE_TREE,
    PlacedFeatures.createCountExtraModifier(1, 0.5F, 1),
    SquarePlacementModifier.of(),
    PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
    BlockFilterPlacementModifier.of(BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.DIRT))
  );

 public static final RegistryEntry<PlacedFeature> CEILING_CAGES = createCeilingFeature(
   "ceiling_cages",
    MineCellsConfiguredFeatures.CEILING_CAGES,
   48
 );

  public static final RegistryEntry<PlacedFeature> CEILING_CHAINS = createCeilingFeature(
    "ceiling_chains",
    MineCellsConfiguredFeatures.CEILING_CHAINS,
    80
  );

  public static final RegistryEntry<PlacedFeature> CEILING_BIG_CHAINS = createCeilingFeature(
    "ceiling_big_chains",
    MineCellsConfiguredFeatures.CEILING_BIG_CHAINS,
    64
  );

  public static final RegistryEntry<PlacedFeature> WILTED_LEAVES = createPlacedFeature(
    MineCells.createId("wilted_leaves"),
    MineCellsConfiguredFeatures.WILTED_LEAVES,
    CountPlacementModifier.of(64),
    SquarePlacementModifier.of(),
    PlacedFeatures.BOTTOM_TO_TOP_RANGE
  );

  public static <FC extends FeatureConfig> RegistryEntry<PlacedFeature> createCeilingFeature(
    String name,
    RegistryEntry<ConfiguredFeature<FC, ?>> feature,
    int count
  ) {
    return createPlacedFeature(
      MineCells.createId(name),
      feature,
      CountPlacementModifier.of(count),
      SquarePlacementModifier.of(),
      PlacedFeatures.BOTTOM_TO_TOP_RANGE,
      EnvironmentScanPlacementModifier.of(
        Direction.UP,
        BlockPredicate.allOf(
          BlockPredicate.matchingBlockTag(MineCellsBlockTags.CEILING_DECORATION_SUPPORT)
        ),
        BlockPredicate.IS_AIR,
        16
      )
    );
  }

  public static <FC extends FeatureConfig> RegistryEntry<PlacedFeature> createPlacedFeature(Identifier id, RegistryEntry<ConfiguredFeature<FC, ?>> feature, PlacementModifier... placementModifiers) {
    List<PlacementModifier> list = new ArrayList<>(List.of(placementModifiers));
    return createPlacedFeature(id, feature, list);
  }

  public static <FC extends FeatureConfig> RegistryEntry<PlacedFeature> createPlacedFeature(Identifier id, RegistryEntry<ConfiguredFeature<FC, ?>> feature, List<PlacementModifier> placementModifiers) {
    return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, id, new PlacedFeature(RegistryEntry.upcast(feature), List.copyOf(placementModifiers)));
  }
}
