package com.github.mim1q.minecells.structure.grid;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class GridBasedStructure extends Structure {
  public static <T extends GridBasedStructure> Codec<T> createGridBasedStructureCodec(
    Function3<Config, HeightProvider, Optional<Heightmap.Type>, T> constructor
  ) {
    return RecordCodecBuilder.<T>mapCodec((instance ->
      instance.group(
        Structure.configCodecBuilder(instance),
        HeightProvider.CODEC.fieldOf("start_height").forGetter(GridBasedStructure::getHeightProvider),
        Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(GridBasedStructure::getProjectStartToHeightmap)
      ).apply(instance, constructor)
    )).codec();
  }

  private final GridPiecesGenerator.RoomGridGenerator generator;
  private List<GridPiece> pieces = new ArrayList<>();
  private final HeightProvider heightProvider;
  private final Optional<Heightmap.Type> projectStartToHeightmap;

  protected GridBasedStructure(Config config, HeightProvider heightProvider, Optional<Heightmap.Type> projectStartToHeightmap, GridPiecesGenerator.RoomGridGenerator generator) {
    super(config);
    this.generator = generator;
    this.heightProvider = heightProvider;
    this.projectStartToHeightmap = projectStartToHeightmap;
  }

  @Override
  public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
    ChunkPos chunkPos = context.chunkPos();
    int x = chunkPos.x * 16;
    int z = chunkPos.z * 16;
    int y = this.heightProvider.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
    int heightmapY = projectStartToHeightmap.map(
      type -> y + context.chunkGenerator().getHeightOnGround(x + 8, z + 8, type, context.world(), context.noiseConfig())
    ).orElse(0);
    BlockPos blockPos = new BlockPos(x, y + heightmapY, z);
    GridPiecesGenerator.RoomGridGenerator generator = this.getGenerator(context);
    if (generator.usesHeightmap()) {
      pieces = GridPiecesGenerator.generateWithHeightmap(blockPos, projectStartToHeightmap, context, 16, generator);
    } else {
      pieces = GridPiecesGenerator.generatePieces(blockPos, context, 16, generator);
    }
    return Optional.of(new Structure.StructurePosition(blockPos, collector -> {
      for (GridPiece piece : pieces) {
        collector.addPiece(piece);
      }
    }));
  }

  protected GridPiecesGenerator.RoomGridGenerator getGenerator(Structure.Context context) {
    return this.generator;
  }

  public HeightProvider getHeightProvider() {
    return heightProvider;
  }

  public Optional<Heightmap.Type> getProjectStartToHeightmap() {
    return projectStartToHeightmap;
  }
}
