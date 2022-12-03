package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.MineCellsStructures;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

public class GridPiece extends StructurePiece {

  private final StructureTemplateManager manager;
  private final StructurePoolElement element;
  private final BlockRotation rotation;
  private final BlockPos pos;
  private final int size;

  public GridPiece(Structure.Context context, Identifier poolId, BlockPos pos, BlockRotation rotation, int size) {
    super(MineCellsStructures.GRID_PIECE, 0, BlockBox.create(pos, pos.add(size, size, size)));
    this.manager = context.structureTemplateManager();
    StructurePool pool = context.dynamicRegistryManager().get(Registry.STRUCTURE_POOL_KEY).get(poolId);
    if (pool == null) {
      throw new RuntimeException("Pool not found: " + poolId);
    }
    this.element = pool.getRandomElement(context.random());
    this.pos = pos;
    this.rotation = rotation;
    this.size = size;
  }

  public GridPiece(StructureTemplateManager manager, StructurePoolElement element, BlockPos pos, BlockRotation rotation, BlockBox boundingBox, int size) {
    super(MineCellsStructures.GRID_PIECE, 0, boundingBox);
    this.manager = manager;
    this.pos = pos;
    this.element = element;
    this.rotation = rotation;
    this.size = size;
  }

  public GridPiece(StructureContext context, NbtCompound nbt) {
    super(MineCellsStructures.GRID_PIECE, nbt);
    this.manager = context.structureTemplateManager();
    this.pos = new BlockPos(nbt.getInt("PosX"), nbt.getInt("PosY"), nbt.getInt("PosZ"));
    this.rotation = BlockRotation.valueOf(nbt.getString("Rot"));
    this.element = StructurePoolElement.CODEC.decode(NbtOps.INSTANCE, nbt.get("Element")).getOrThrow(false, MineCells.LOGGER::error).getFirst();
    this.size = nbt.getInt("Size");
  }

  @Override
  protected void writeNbt(StructureContext context, NbtCompound nbt) {
    nbt.putInt("PosX", pos.getX());
    nbt.putInt("PosY", pos.getY());
    nbt.putInt("PosZ", pos.getZ());
    nbt.putString("Rot", rotation.name());
    nbt.put("Element", StructurePoolElement.CODEC.encodeStart(NbtOps.INSTANCE, element).getOrThrow(false, MineCells.LOGGER::error));
    nbt.putInt("Size", size);
  }

  @Override
  public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
    this.element.generate(this.manager, world, structureAccessor, chunkGenerator, this.pos, this.pos, this.rotation, chunkBox, random, false);
  }

  @Override
  public StructurePieceType getType() {
    return MineCellsStructures.GRID_PIECE;
  }
}
