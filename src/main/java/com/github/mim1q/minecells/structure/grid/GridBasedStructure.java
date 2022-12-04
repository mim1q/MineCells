package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.structure.MineCellsStructures;
import com.mojang.serialization.Codec;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.List;
import java.util.Optional;

public class GridBasedStructure extends Structure {
  public static final Codec<GridBasedStructure> CODEC = createCodec(GridBasedStructure::new);

  protected GridBasedStructure(Config config) {
    super(config);
  }

  @Override
  public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
    return Optional.of(new Structure.StructurePosition(context.chunkPos().getStartPos(), collector -> {
      addPieces(collector, context);
    }));
  }

  private static void addPieces(StructurePiecesCollector collector, Structure.Context context) {
    ChunkPos chunkPos = context.chunkPos();
    int i = chunkPos.x;
    int j = chunkPos.z;
    int k = i * 16;
    int l = j * 16;
    BlockPos blockPos = new BlockPos(k, 30, l);
    List<GridPiece> pieces = GridPiecesGenerator.generatePieces(blockPos, context, 16);
    for (GridPiece piece : pieces) {
      collector.addPiece(piece);
    }
  }

  @Override
  public StructureType<?> getType() {
    return MineCellsStructures.GRID_BASED_STRUCTURE;
  }
}
