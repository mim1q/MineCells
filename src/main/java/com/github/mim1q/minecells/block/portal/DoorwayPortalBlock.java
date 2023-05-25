package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.block.FillerBlock;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class DoorwayPortalBlock extends Block {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
  private final Filler filler;

  public DoorwayPortalBlock(Settings settings, Filler filler) {
    super(settings);
    this.filler = filler;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), SHAPE);
  }

  public void place(ServerWorld world, BlockPos pos, Direction direction) {
    world.setBlockState(pos.add(0, 1, 0), getDefaultState().with(FACING, direction));
    world.setBlockState(pos, filler.getState(Filler.FillerType.MIDDLE, direction));
    world.setBlockState(pos.add(0, 2, 0), filler.getState(Filler.FillerType.MIDDLE, direction));
    world.setBlockState(pos, filler.getState(Filler.FillerType.MIDDLE, direction));

  }

  public static class Filler extends FillerBlock {
    private static final EnumProperty<FillerType> TYPE = EnumProperty.of("type", FillerType.class);

    public Filler(Settings settings, Block targetBlock) {
      super(settings, targetBlock, true);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), state.get(TYPE).outlineShape);
    }

    public BlockState getState(FillerType type, Direction direction) {
      return getDefaultState().with(FACING, direction).with(TYPE, type);
    }

    @Override
    public float getHardness() {
      return super.getHardness();
    }

    private enum FillerType implements StringIdentifiable {
      MIDDLE(SHAPE, VoxelShapes.empty(), "middle"),
      LEFT(
        Block.createCuboidShape(4.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
        VoxelShapes.empty(),
        "left"
      ),
      RIGHT(
        Block.createCuboidShape(0.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D),
        VoxelShapes.empty(),
        "right"
      );

      public final VoxelShape outlineShape;
      public final VoxelShape collisionShape;
      private final String name;
      FillerType(VoxelShape outlineShape, VoxelShape collisionShape, String name) {
        this.outlineShape = outlineShape;
        this.collisionShape = collisionShape;
        this.name = name;
      }

      @Override
      public String asString() {
        return name;
      }
    }
  }
}
