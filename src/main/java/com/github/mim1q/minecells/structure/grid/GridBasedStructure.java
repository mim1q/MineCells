package com.github.mim1q.minecells.structure.grid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class GridBasedStructure extends Structure {
  public static <T extends GridBasedStructure> Codec<T> createGridBasedStructureCodec(
    BiFunction<Config, HeightProvider, T> constructor
  ) {
    return RecordCodecBuilder.<T>mapCodec((instance ->
      instance.group(
        Structure.configCodecBuilder(instance),
        HeightProvider.CODEC.fieldOf("start_height").forGetter(GridBasedStructure::getHeightProvider)
      )
        .apply(instance, constructor)
    )).codec();
  }

  private final GridPiecesGenerator.RoomGridGenerator generator;
  private final HeightProvider heightProvider;

  protected GridBasedStructure(Config config, HeightProvider heightProvider, GridPiecesGenerator.RoomGridGenerator generator) {
    super(config);
    this.generator = generator;
    this.heightProvider = heightProvider;
  }

  @Override
  public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
    return Optional.of(new Structure.StructurePosition(context.chunkPos().getStartPos(), collector -> addPieces(collector, context)));
  }

  private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
    ChunkPos chunkPos = context.chunkPos();
    int x = chunkPos.x * 16;
    int z = chunkPos.z * 16;
    int y = this.heightProvider.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
    BlockPos blockPos = new BlockPos(x, y, z);
    List<GridPiece> pieces = GridPiecesGenerator.generatePieces(blockPos, context, 16, this.generator);
    for (GridPiece piece : pieces) {
      collector.addPiece(piece);
    }
  }

  public HeightProvider getHeightProvider() {
    return heightProvider;
  }
}
