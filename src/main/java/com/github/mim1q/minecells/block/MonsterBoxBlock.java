package com.github.mim1q.minecells.block;

import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class MonsterBoxBlock extends Block {

  private final List<Entry> entries = new ArrayList<>();

  public MonsterBoxBlock(Entry ...entries) {
    super(Settings.of(Material.STONE));
    this.entries.addAll(List.of(entries));
  }

  public MonsterBoxBlock(EntityType<?> entityType) {
    this(new Entry(entityType, 1.0F, 1));
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return VoxelShapes.empty();
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @Override
  public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
    return false;
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (world instanceof ServerWorldAccess serverWorldAccess) {
      ServerWorld serverWorld = serverWorldAccess.toServerWorld();
      onBlockAdded(state, serverWorld, pos, state, false);
    }
    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
  }

  @Override
  public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    world.removeBlock(pos, false);
    for (Entry entry : entries) {
      if (world.getRandom().nextFloat() <= entry.chance) {
        for (int i = 0; i < entry.count; i++) {
          HostileEntity e = (HostileEntity) entry.entityType.create(world);
          if (e != null) {
            e.setPersistent();
            e.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            e.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
            world.spawnEntity(e);
          }
        }
      }
    }
  }

  public record Entry(EntityType<?> entityType, float chance, int count) {
  }
}
