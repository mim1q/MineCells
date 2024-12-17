package com.github.mim1q.minecells.structure.grid;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;

public class GridBasedStructureUtils {
  public static List<GridPiecesGenerator.RoomGridGenerator.SpecialPoint> getSpecialPoints(
    ServerWorld world,
    BlockPos structureOrigin,
    GridPiecesGenerator.RoomGridGenerator generator
  ) {
    var registryManager = world.getRegistryManager();
    var chunkGenerator = world.getChunkManager().getChunkGenerator();
    var noiseConfig = world.getChunkManager().getNoiseConfig();
    var structureTemplateManager = world.getStructureTemplateManager();
    var seed = world.getSeed();
    var context = new Structure.Context(
      registryManager,
      chunkGenerator,
      chunkGenerator.getBiomeSource(),
      noiseConfig,
      structureTemplateManager,
      seed,
      new ChunkPos(structureOrigin),
      world,
      (registryEntry) -> true
    );

    return generator.generateSpecialPoints(context);
  }
}
