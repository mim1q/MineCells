package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.MineCellsStructures;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
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

public class GridPiece extends StructurePiece {

  private final StructureTemplateManager manager;
  private final StructurePoolElement element;
  private final BlockRotation rotation;
  private final BlockPos pos;

  protected GridPiece(StructureTemplateManager manager, StructurePoolElement element, BlockPos pos, BlockRotation rotation, BlockBox boundingBox) {
    super(MineCellsStructures.GRID_PIECE, 0, boundingBox);
    this.manager = manager;
    this.pos = pos;
    this.element = element;
    this.rotation = rotation;
  }

  public GridPiece(StructureContext context, NbtCompound nbt) {
    super(MineCellsStructures.GRID_PIECE, nbt);
    this.manager = context.structureTemplateManager();
    this.pos = new BlockPos(nbt.getInt("PosX"), nbt.getInt("PosY"), nbt.getInt("PosZ"));
    this.rotation = BlockRotation.valueOf(nbt.getString("Rot"));
    this.element = StructurePoolElement.CODEC.decode(NbtOps.INSTANCE, nbt.get("Element")).getOrThrow(false, MineCells.LOGGER::error).getFirst();
  }

  @Override
  protected void writeNbt(StructureContext context, NbtCompound nbt) {
    nbt.putInt("PosX", pos.getX());
    nbt.putInt("PosY", pos.getY());
    nbt.putInt("PosZ", pos.getZ());
    nbt.putString("Rot", rotation.name());
    nbt.put("Element", StructurePoolElement.CODEC.encodeStart(NbtOps.INSTANCE, element).getOrThrow(false, MineCells.LOGGER::error));
  }

  @Override
  public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
    for (int y = 1; y < 31; y++) {
      for (int x = 1; x < 31; x++) {
        for (int z = 1; z < 31; z++) {
          BlockPos pos = new BlockPos(this.pos.getX() + x, this.pos.getY() + y, this.pos.getZ() + z);
          addBlock(world, AIR, pos.getX(), pos.getY(), pos.getZ(), chunkBox);
        }
      }
    }
    this.element.generate(this.manager, world, structureAccessor, chunkGenerator, this.pos, this.pos, this.rotation, chunkBox, random, false);
  }

  @Override
  public StructurePieceType getType() {
    return MineCellsStructures.GRID_PIECE;
  }
}
