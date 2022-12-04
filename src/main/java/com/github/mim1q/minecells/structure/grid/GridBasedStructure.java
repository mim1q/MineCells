package com.github.mim1q.minecells.structure.grid;

import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;
import java.util.Optional;

public abstract class GridBasedStructure extends Structure {
  private final GridPiecesGenerator.RoomGridGenerator generator;

  protected GridBasedStructure(Config config, GridPiecesGenerator.RoomGridGenerator generator) {
    super(config);
    this.generator = generator;
  }

  @Override
  public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
    return Optional.of(new Structure.StructurePosition(context.chunkPos().getStartPos(), collector -> addPieces(collector, context)));
  }

  private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
    ChunkPos chunkPos = context.chunkPos();
    int i = chunkPos.x;
    int j = chunkPos.z;
    int k = i * 16;
    int l = j * 16;
    BlockPos blockPos = new BlockPos(k, 30, l);
    List<GridPiece> pieces = GridPiecesGenerator.generatePieces(blockPos, context, 16, this.generator);
    for (GridPiece piece : pieces) {
      collector.addPiece(piece);
    }
  }
}
