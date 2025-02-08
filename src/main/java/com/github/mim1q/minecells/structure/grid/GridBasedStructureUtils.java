package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator.SpecialPoint;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.structure.Structure;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class GridBasedStructureUtils {
  private static long prevSeed = 0;
  private static final HashMap<Pair<GridPiecesGenerator.RoomGridGenerator, Vec3i>, List<SpecialPoint>> SPECIAL_POINTS_CACHE = new HashMap<>();

  public static List<SpecialPoint> getSpecialPoints(
    ServerWorld world,
    Vec3i structureOrigin,
    GridPiecesGenerator.RoomGridGenerator generator
  ) {
    var seed = world.getSeed();
    if (prevSeed != seed) {
      SPECIAL_POINTS_CACHE.clear();
      prevSeed = seed;
    }

    var pos = MathUtils.getClosestMultiplePosition(structureOrigin, 1024);
    return SPECIAL_POINTS_CACHE.computeIfAbsent(new Pair<>(generator, pos), it -> {
      var registryManager = world.getRegistryManager();
      var chunkGenerator = world.getChunkManager().getChunkGenerator();
      var noiseConfig = world.getChunkManager().getNoiseConfig();
      var structureTemplateManager = world.getStructureTemplateManager();
      var context = new Structure.Context(
        registryManager,
        chunkGenerator,
        chunkGenerator.getBiomeSource(),
        noiseConfig,
        structureTemplateManager,
        seed,
        new ChunkPos(new BlockPos(structureOrigin)),
        world,
        (registryEntry) -> true
      );

      return generator.generateSpecialPoints(context);
    });
  }

  public static Optional<SpecialPoint> getSpecialPoint(
    ServerWorld world,
    Vec3i structureOrigin,
    GridPiecesGenerator.RoomGridGenerator generator,
    Identifier id
  ) {
    for (var point : getSpecialPoints(world, structureOrigin, generator)) {
      if (point.id().equals(id)) {
        return Optional.of(point);
      }
    }

    return Optional.empty();
  }

  public static Optional<SpecialPoint> getSpecialPoint(
    ServerWorld world,
    Vec3i structureOrigin,
    MineCellsDimension dimension,
    Identifier id
  ) {
    return getSpecialPoint(world, structureOrigin, dimension.baseGenerator, id);
  }

  public static Optional<SpecialPoint> getSpecialPoint(
    ServerWorld world,
    Vec3i structureOrigin,
    Identifier id
  ) {
    var dimension = MineCellsDimension.of(world);
    if (dimension == null) {
      return Optional.empty();
    }

    return getSpecialPoint(world, structureOrigin, dimension, id);
  }

  public static void clearSpecialPointsCache() {
    SPECIAL_POINTS_CACHE.clear();
  }
}
