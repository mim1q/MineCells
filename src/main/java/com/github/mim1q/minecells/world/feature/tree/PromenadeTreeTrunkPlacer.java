package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class PromenadeTreeTrunkPlacer extends StraightTrunkPlacer {
  public static final Codec<PromenadeTreeTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
    return fillTrunkPlacerFields(instance).apply(instance, PromenadeTreeTrunkPlacer::new);
  });

  public PromenadeTreeTrunkPlacer(int i, int j, int k) {
    super(i, j, k);
  }

  @Override
  public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
    setToDirt(world, replacer, random, startPos.down(), config);
    Direction lastDirection = null;
    for (int i = 0; i < height; ++i) {
      this.getAndSetState(world, replacer, random, startPos.up(i), config);
      if (i > 3 && i < height - 2 && random.nextFloat() < 0.2F) {
        lastDirection = this.placeBranchAndGetDirection(lastDirection, world, replacer, random, i, startPos, config);
      }
    }

    return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(height), 0, false));
  }

  public Direction placeBranchAndGetDirection(Direction lastDirection, TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
    BlockPos position = startPos.up(height);
    Direction direction = null;
    while (direction == null || direction == lastDirection) {
      direction = switch (random.nextInt(4)) {
        case 0 -> Direction.NORTH;
        case 1 -> Direction.EAST;
        case 2 -> Direction.SOUTH;
        default -> Direction.WEST;
      };
    }
    Vec3i offset = direction.getVector();
    position = position.add(offset);
    Block block = config.trunkProvider.getBlockState(random, position).getBlock();
    BlockState state = block.getDefaultState();
    if (block instanceof PillarBlock) {
      Direction.Axis axis = (direction == Direction.SOUTH || direction == Direction.NORTH)
                              ? Direction.Axis.Z
                              : Direction.Axis.X;
      state = state.with(PillarBlock.AXIS, axis);
    }
    if (this.canReplace(world, position)) {
      replacer.accept(position, state);
      if (random.nextFloat() < 0.2F) {
        this.placeChain(replacer, random, height, position);
      }
    }
    return direction;
  }

  public void placeChain(BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos) {
    float randomHeight = random.nextInt(height - 1) + 1;
    for (int i = 1; i <= randomHeight; i++) {
      replacer.accept(startPos.down(i), Blocks.CHAIN.getDefaultState());
    }
  }

  @Override
  protected TrunkPlacerType<?> getType() {
    return MineCellsPlacerTypes.PROMENADE_TRUNK_PLACER;
  }
}
