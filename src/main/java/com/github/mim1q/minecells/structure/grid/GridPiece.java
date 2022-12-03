package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.structure.MineCellsStructures;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class GridPiece extends PoolStructurePiece {

  public GridPiece(
    StructureTemplateManager structureTemplateManager,
    StructurePoolElement poolElement,
    BlockPos pos,
    int groundLevelDelta,
    BlockRotation rotation,
    BlockBox boundingBox
  ) {
    super(structureTemplateManager, poolElement, pos, groundLevelDelta, rotation, boundingBox);
  }

  public GridPiece(StructureContext context, NbtCompound nbt) {
    super(context, nbt);
  }

  @Override
  protected void writeNbt(StructureContext context, NbtCompound nbt) {
    nbt.putInt("PosX", pos.getX());
    nbt.putInt("PosY", pos.getY());
    nbt.putInt("PosZ", pos.getZ());
  }

  @Override
  public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
    for (int y = 1; y < 31; y++) {
      for (int x = 1; x < 31; x++) {
        for (int z = 1; z < 31; z++) {
          BlockPos pos = new BlockPos(this.pos.getX() + x, this.pos.getY() + y, this.pos.getZ() + z);
          if (chunkBox.contains(pos)) {
            world.setBlockState(pos, AIR, 2);
          }
        }
      }
    }
    super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
  }

  @Override
  public StructurePieceType getType() {
    return MineCellsStructures.GRID_PIECE;
  }
}
