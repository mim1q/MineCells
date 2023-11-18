package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class PromenadeShrubTrunkPlacer extends TrunkPlacer {
  public static final Codec<PromenadeShrubTrunkPlacer> CODEC = RecordCodecBuilder.create(
    (instance) -> fillTrunkPlacerFields(instance).apply(instance, PromenadeShrubTrunkPlacer::new)
  );

  public PromenadeShrubTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
    super(baseHeight, firstRandomHeight, secondRandomHeight);
  }

  @Override
  public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
   if (world.testBlockState(startPos, BlockState::isAir)) {
     replacer.accept(startPos, config.trunkProvider.get(random, startPos));
   }
    return List.of(new FoliagePlacer.TreeNode(startPos.up(), 0, false));
  }

  @Override
  protected TrunkPlacerType<?> getType() {
    return MineCellsPlacerTypes.PROMENADE_SHRUB_TRUNK;
  }
}
