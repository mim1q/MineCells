package com.github.mim1q.minecells.structure;

import com.github.mim1q.minecells.structure.grid.MineCellsStructurePoolBasedGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MineCellsBigJigsawStructure extends Structure {
  public static Codec<MineCellsBigJigsawStructure> CODEC = RecordCodecBuilder.<MineCellsBigJigsawStructure>mapCodec((instance) ->
    instance
      .group(
        MineCellsBigJigsawStructure.configCodecBuilder(instance),
        StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter((structure) -> structure.startPool),
        Codec.intRange(0, 100).fieldOf("size").forGetter((structure) -> structure.size),
        HeightProvider.CODEC.fieldOf("start_height").forGetter((structure) -> structure.startHeight),
        Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((structure) -> structure.projectStartToHeightmap),
        Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter((structure) -> structure.maxDistanceFromCenter)
      )
      .apply(instance, MineCellsBigJigsawStructure::new)).codec();

  private final RegistryEntry<StructurePool> startPool;
  private final int size;
  private final HeightProvider startHeight;
  private final int maxDistanceFromCenter;
  private final Optional<Heightmap.Type> projectStartToHeightmap;

  protected MineCellsBigJigsawStructure(
    Config config,
    RegistryEntry<StructurePool> startPool,
    int size,
    HeightProvider startHeight,
    Optional<Heightmap.Type> projectStartToHeightmap,
    int maxDistanceFromCenter
  ) {
    super(config);
    this.startPool = startPool;
    this.size = size;
    this.startHeight = startHeight;
    this.projectStartToHeightmap = projectStartToHeightmap;
    this.maxDistanceFromCenter = maxDistanceFromCenter;
  }

  @Override
  public Optional<StructurePosition> getStructurePosition(Context context) {
    ChunkPos chunkPos = context.chunkPos();
    BlockPos pos = chunkPos.getStartPos().withY(this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world())));

    return MineCellsStructurePoolBasedGenerator.generate(
      context,
      this.startPool,
      Optional.empty(),
      this.size,
      pos,
      this.projectStartToHeightmap,
      BlockRotation.NONE
    );
  }

  @Override
  public StructureType<?> getType() {
    return MineCellsStructures.BIG_JIGSAW;
  }
}
