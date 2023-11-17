package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.structure.MineCellsStructures;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;

public class GridPiece extends StructurePiece {

  private final DynamicRegistryManager registryManager;
  private final StructureTemplateManager manager;
  private final Identifier template;
  private final BlockRotation rotation;
  private final BlockPos pos;
  private final int size;

  public GridPiece(Structure.Context context, Identifier poolId, BlockPos pos, BlockRotation rotation, int size) {
    super(MineCellsStructures.GRID_PIECE, 0, BlockBox.create(pos, pos.add(size, size, size)));
    this.manager = context.structureTemplateManager();
    StructurePool pool = context.dynamicRegistryManager().get(RegistryKeys.TEMPLATE_POOL).get(poolId);
    if (pool == null) {
      throw new RuntimeException("Pool not found: " + poolId);
    }
    this.pos = pos;
    this.rotation = rotation;
    this.size = size;
    this.registryManager = context.dynamicRegistryManager();
    this.template = poolId;
  }

  public GridPiece(StructureContext context, NbtCompound nbt) {
    super(MineCellsStructures.GRID_PIECE, nbt);
    this.manager = context.structureTemplateManager();
    this.pos = new BlockPos(nbt.getInt("PosX"), nbt.getInt("PosY"), nbt.getInt("PosZ"));
    this.rotation = BlockRotation.valueOf(nbt.getString("Rot"));
    this.size = nbt.getInt("Size");
    this.registryManager = context.registryManager();
    this.template = new Identifier(nbt.getString("Template"));
  }

  @Override
  protected void writeNbt(StructureContext context, NbtCompound nbt) {
    nbt.putInt("PosX", pos.getX());
    nbt.putInt("PosY", pos.getY());
    nbt.putInt("PosZ", pos.getZ());
    nbt.putString("Rot", rotation.name());
    nbt.putInt("Size", size);
    nbt.putString("Template", template.toString());
  }

  @Override
  public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
    BlockPos startingPos = this.getStartingPos();
    NoiseConfig config = world.getChunkManager() instanceof ServerChunkManager ? ((ServerChunkManager) world.getChunkManager()).getNoiseConfig() : null;
    Registry<StructurePool> poolRegistry = registryManager.get(RegistryKeys.TEMPLATE_POOL);
    var optPoolEntry = poolRegistry.getEntry(RegistryKey.of(RegistryKeys.TEMPLATE_POOL, this.template));
    if (optPoolEntry.isEmpty()) {
      return;
    }
    RegistryEntry<StructurePool> poolEntry = optPoolEntry.get();
    MineCellsStructurePoolBasedGenerator.generate(
      world,
      chunkGenerator,
      registryManager,
      manager,
      structureAccessor,
      config,
      random,
      (int)world.getSeed() + this.pos.hashCode(),
      poolEntry,
      8,
      startingPos,
      rotation
    );
  }

  private BlockPos getStartingPos() {
    return switch (rotation) {
      case CLOCKWISE_90 -> new BlockPos(pos.getX() + size - 1, pos.getY(), pos.getZ());
      case CLOCKWISE_180 -> new BlockPos(pos.getX() + size - 1, pos.getY(), pos.getZ() + size - 1);
      case COUNTERCLOCKWISE_90 -> new BlockPos(pos.getX(), pos.getY(), pos.getZ() + size - 1);
      default -> pos;
    };
  }

  @Override
  public StructurePieceType getType() {
    return MineCellsStructures.GRID_PIECE;
  }
}
