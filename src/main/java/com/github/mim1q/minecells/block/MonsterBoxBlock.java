package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class MonsterBoxBlock extends Block {

  private final EntityType<?> entityType;

  public MonsterBoxBlock(EntityType<?> entityType) {
    super(Settings.of(Material.STONE));
    this.entityType = entityType;
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return VoxelShapes.empty();
  }

  @Override
  public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
    return false;
  }

  @Override
  public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    world.removeBlock(pos, false);
    HostileEntity e = (HostileEntity) this.entityType.create(world);
    if (e != null) {
      e.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      e.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
      world.spawnEntity(e);
    }
  }
}
