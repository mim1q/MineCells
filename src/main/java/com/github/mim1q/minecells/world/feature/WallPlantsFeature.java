package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.block.MineCellsBlockTags;
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
import java.util.Optional;

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
      if (state.isIn(MineCellsBlockTags.VEGETATION_REPLACEABLE) && state.isSideSolidFullSquare(world, newPos, direction.getOpposite())) {
        return direction.getOpposite();
      }
    }
    return null;
  }

  public static void placePlant(WorldAccess world, BlockPos pos, BlockStateProvider provider, Random rng, Direction dir) {
    var state = provider.getBlockState(rng, pos).with(WallLeavesBlock.DIRECTION, dir);
    world.setBlockState(pos, state, 2);
  }

  public static void placeLeaf(WorldAccess world, BlockPos pos, BlockStateProvider provider, Random rng, Direction dir) {
    var offsetPos = pos.add(dir.getOpposite().getVector());
    var state = provider.getBlockState(rng, offsetPos);
    world.setBlockState(offsetPos, state, 2);
  }

  public static boolean generateAtPosition(
    WorldAccess world,
    BlockPos pos,
    BlockStateProvider plantStateProvider,
    BlockStateProvider leafStateProvider,
    Random rng,
    boolean spawnPlant,
    boolean spawnLeaf
  ) {
    if (!world.getBlockState(pos).isAir()) {
      return false;
    }
    var direction = getDirection(world, pos);
    if (direction == null) {
      return false;
    }

    if (spawnPlant) {
      placePlant(world, pos, plantStateProvider, rng, direction);
    }
    if (spawnLeaf && leafStateProvider != null) {
      placeLeaf(world, pos, leafStateProvider, rng, direction);
    }

    return true;
  }

  @Override
  public boolean generate(FeatureContext<WallPlantsFeatureConfig> context) {
    var origin = context.getOrigin();
    var rng = context.getRandom();
    var radius = context.getConfig().radius.get(rng);
    var positions = getPositions(origin, radius);
    boolean generated = false;
    for (BlockPos pos : positions) {
      if (!pos.isWithinDistance(origin, radius)) {
        continue;
      }
      boolean placePlant = rng.nextFloat() <= context.getConfig().chance;
      boolean placeLeaf = placePlant && rng.nextFloat() <= context.getConfig().chance;
      boolean blockPlaced = generateAtPosition(
        context.getWorld(),
        pos,
        context.getConfig().plantStateProvider,
        context.getConfig().leafStateProvider.orElse(null),
        rng,
        placePlant,
        placeLeaf
      );
      generated = generated || blockPlaced;
    }
    return generated;
  }

  public record WallPlantsFeatureConfig(
    BlockStateProvider plantStateProvider,
    Optional<BlockStateProvider> leafStateProvider,
    IntProvider radius,
    float chance,
    float leafChance
    ) implements FeatureConfig {
    public static final Codec<WallPlantsFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
      BlockStateProvider.TYPE_CODEC.fieldOf("plant_state_provider").forGetter((config) -> config.plantStateProvider),
      BlockStateProvider.TYPE_CODEC.optionalFieldOf("leaf_state_provider").forGetter((config) -> config.leafStateProvider),
      IntProvider.POSITIVE_CODEC.fieldOf("radius").forGetter((config) -> config.radius),
      Codec.FLOAT.fieldOf("chance").forGetter((config) -> config.chance),
      Codec.FLOAT.fieldOf("leafChance").forGetter((config) -> config.leafChance)
    ).apply(instance, WallPlantsFeatureConfig::new));
  }
}
