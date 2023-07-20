package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.block.FillerBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
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
import org.jetbrains.annotations.Nullable;

public class TeleporterBlock extends BlockWithEntity {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private static final VoxelShape SHAPE = createCuboidShape(0, 0, 6, 16, 16, 10);

  public TeleporterBlock(Settings settings) {
    super(settings.nonOpaque().luminance(state -> 8));
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new TeleporterBlockEntity(pos, state);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.SOUTH, state.get(FACING), SHAPE);
  }

  public static class Filler extends FillerBlock {
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    public Filler(Settings settings) {
      super(settings.nonOpaque().luminance(state -> 8), MineCellsBlocks.TELEPORTER_CORE, true);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(TYPE, FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return state.get(TYPE) == Type.MIDDLE ? VoxelShapes.empty() : super.getCollisionShape(state, world, pos, context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return ModelUtils.rotateShape(Direction.SOUTH, state.get(FACING), state.get(TYPE).shape);
    }

    public enum Type implements StringIdentifiable {
      BOTTOM("bottom", VoxelShapes.union(
        Block.createCuboidShape(0, 4, 3, 6, 14, 13),
        Block.createCuboidShape(4, 0, 3, 16, 10, 13)
      )),

      TOP("top", VoxelShapes.union(
        Block.createCuboidShape(0, 2, 3, 6, 12, 13),
        Block.createCuboidShape(4, 6, 3, 16, 16, 13)
      )),

      SIDE_LOWER("side_lower", VoxelShapes.union(
        Block.createCuboidShape(0, 4, 3, 10, 16, 13),
        Block.createCuboidShape(4, 0, 3, 14, 6, 13)
      )),

      SIDE_UPPER("side_upper", VoxelShapes.union(
        Block.createCuboidShape(0, 0, 3, 10, 12, 13),
        Block.createCuboidShape(4, 10, 3, 14, 16, 13)
      )),

      CORNER_LOWER("corner_lower", VoxelShapes.union(
        Block.createCuboidShape(4, 10, 3, 14, 16, 13),
        Block.createCuboidShape(10, 4, 3, 16, 14, 13)
      )),

      CORNER_UPPER("corner_upper", VoxelShapes.union(
        Block.createCuboidShape(4, 0, 3, 14, 6, 13),
        Block.createCuboidShape(10, 2, 3, 16, 12, 13)
      )),

      MIDDLE("middle", SHAPE);

      private final String name;
      public final VoxelShape shape;

      Type(String name, VoxelShape shape) {
        this.name = name;
        this.shape = shape;
      }

      @Override
      public String asString() {
        return name;
      }
    }
  }
}
