package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class GroundDecoration extends Block {

  private final Shape shape;
  public GroundDecoration(Settings settings, Shape shape) {
    super(settings);
    this.shape = shape;
  }

  @Override
  public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return this.shape.getShape();
  }

  public enum Shape {
    PILE(VoxelShapes.cuboid(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D)),
    BLOCK_12(VoxelShapes.cuboid(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D)),
    BARREL(Block.createCuboidShape(1.5D, 0.0D, 1.5D, 14.5D, 18.0D, 14.5D));
    private final VoxelShape shape;

    Shape(VoxelShape shape) {
      this.shape = shape;
    }

    public VoxelShape getShape() {
      return shape;
    }
  }
}
