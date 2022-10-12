package com.github.mim1q.minecells.structure;

import com.github.mim1q.minecells.structure.generator.BigDungeonPoolBasedGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class BigDungeonStructure extends Structure {
  public static Codec<BigDungeonStructure> CODEC = RecordCodecBuilder.<BigDungeonStructure>mapCodec((instance) ->
    instance
      .group(
        BigDungeonStructure.configCodecBuilder(instance),
        StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter((structure) -> structure.startPool),
        Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((structure) -> structure.startJigsawName),
        Codec.intRange(0, 100).fieldOf("size").forGetter((structure) -> structure.size),
        Codec.intRange(-64, 320).fieldOf("y").forGetter((structure) -> structure.y),
        Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter((structure) -> structure.maxDistanceFromCenter)
      )
      .apply(instance, BigDungeonStructure::new)).codec();

  private final RegistryEntry<StructurePool> startPool;
  private final Optional<Identifier> startJigsawName;
  private final int size;
  private final int y;
  private final int maxDistanceFromCenter;

  protected BigDungeonStructure(
    Config config,
    RegistryEntry<StructurePool> startPool,
    Optional<Identifier> startJigsawName,
    int size,
    int y,
    int maxDistanceFromCenter
  ) {
    super(config);
    this.startPool = startPool;
    this.startJigsawName = startJigsawName;
    this.size = size;
    this.y = y;
    this.maxDistanceFromCenter = maxDistanceFromCenter;
  }

  private static boolean extraSpawnConditions(Structure.Context context) {
    ChunkPos chunkPos = context.chunkPos();

    boolean inGrid = MathHelper.abs(chunkPos.x) % 8 == 0 && MathHelper.abs(chunkPos.z) % 8 == 0;

    if (!inGrid) {
      return false;
    }
    int x = chunkPos.getCenterX();
    int z = chunkPos.getCenterZ();
    int nearestX = Math.round(x / 4096.0F) * 4096;
    int nearestZ = Math.round(z / 4096.0F) * 4096;
    int distance = Math.max(Math.abs(nearestX - x), Math.abs(nearestZ - z));

    return distance <= 1024;
  }

  @Override
  public Optional<StructurePosition> getStructurePosition(Context context) {
    if (!extraSpawnConditions(context)) {
      return Optional.empty();
    }

    ChunkPos chunkPos = context.chunkPos();
    BlockPos pos = chunkPos.getStartPos().withY(this.y);

    return BigDungeonPoolBasedGenerator.generate(
      context,
      this.startPool,
      this.startJigsawName,
      this.size,
      pos,
      false,
      Optional.empty(),
      this.maxDistanceFromCenter
    );
  }

  @Override
  public void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos, StructurePiecesList pieces) {
    super.postPlace(world, structureAccessor, chunkGenerator, random, box, chunkPos, pieces);
  }

  @Override
  public StructureType<?> getType() {
    return MineCellsStructures.BIG_DUNGEON;
  }
}
