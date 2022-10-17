package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.block.WallLeavesBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.*;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WallPlantsFeature extends Feature<WallPlantsFeature.WallPlantsFeatureConfig> {

  public WallPlantsFeature(Codec<WallPlantsFeatureConfig> configCodec) {
    super(configCodec);
  }

  public Iterable<BlockPos> getPositions(Vec3i pos, int radius) {
    BlockBox box = BlockBox.create(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));
    return BlockPos.iterate(
      box.getMinX(), box.getMinY(), box.getMinZ(),
      box.getMaxX(), box.getMaxY(), box.getMaxZ()
    );
  }

  public static Direction getDirection(WorldView world, BlockPos pos) {
    ChunkPos currentChunkPos = world.getChunk(pos).getPos();
    List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
    Collections.shuffle(directions);
    for (Direction direction : directions) {
      BlockPos newPos = pos.offset(direction);
      if (!world.getChunk(newPos).getPos().equals(currentChunkPos)) {
        continue;
      }
      BlockState state = world.getBlockState(newPos);
      if (state.isFullCube(world, newPos) && state.isSideSolidFullSquare(world, newPos, direction.getOpposite())) {
        return direction.getOpposite();
      }
    }
    return null;
  }

  public boolean generateAtPosition(WorldAccess world, BlockPos pos, BlockStateProvider stateProvider, Random rng) {
    if (!world.getBlockState(pos).isAir()) {
      return false;
    }
    var direction = getDirection(world, pos);
    if (direction == null) {
      return false;
    }

    var state = stateProvider.getBlockState(rng, pos).with(WallLeavesBlock.DIRECTION, direction);
    world.setBlockState(pos, state, 2);
    return true;
  }

  @Override
  public boolean generate(FeatureContext<WallPlantsFeatureConfig> context) {
    var origin = context.getOrigin();
    var radius = context.getConfig().radius.get(context.getRandom());
    var positions = getPositions(origin, radius);
    boolean generated = false;
    for (BlockPos pos : positions) {
      if (!pos.isWithinDistance(origin, radius)) {
        continue;
      }
      boolean blockPlaced = generateAtPosition(context.getWorld(), pos, context.getConfig().plant, context.getRandom());
      generated = generated || blockPlaced;
    }
    return generated;
  }

  public record WallPlantsFeatureConfig(
    BlockStateProvider plant,
    IntProvider radius
  ) implements FeatureConfig {
    public static final Codec<WallPlantsFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
      BlockStateProvider.TYPE_CODEC.fieldOf("plant").forGetter((config) -> config.plant),
      IntProvider.POSITIVE_CODEC.fieldOf("radius").forGetter((config) -> config.radius)
    ).apply(instance, WallPlantsFeatureConfig::new));
  }
}
