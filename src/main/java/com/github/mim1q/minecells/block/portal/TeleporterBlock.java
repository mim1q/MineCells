package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.block.FillerBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class TeleporterBlock extends BlockWithEntity {
  private static final VoxelShape SHAPE = createCuboidShape(0, 0, 7, 16, 16, 9);

  public TeleporterBlock(Settings settings) {
    super(settings);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new TeleporterBlockEntity(pos, state);
  }

  public static class Filler extends FillerBlock {
    private static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    public Filler(Settings settings) {
      super(settings, MineCellsBlocks.TELEPORTER_CORE, true);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(TYPE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return state.get(TYPE) == Type.MIDDLE ? VoxelShapes.empty() : super.getCollisionShape(state, world, pos, context);
    }

    public enum Type implements StringIdentifiable {
      BOTTOM("bottom", VoxelShapes.union(
        Block.createCuboidShape(3, 0, 0, 13, 10, 13),
        Block.createCuboidShape(3, 3, 11, 13, 14, 16)
      )),

      TOP("top", VoxelShapes.union(
        Block.createCuboidShape(3, 6, 0, 13, 16, 13),
        Block.createCuboidShape(3, 2, 11, 13, 13, 16)
      )),

      SIDE_LOWER("side_lower", VoxelShapes.union(
        Block.createCuboidShape(3, 0, 2, 13, 4, 13),
        Block.createCuboidShape(3, 2, 6, 13, 16, 16)
      )),

      SIDE_UPPER("side_upper", VoxelShapes.union(
        Block.createCuboidShape(3, 12, 2, 13, 16, 13),
        Block.createCuboidShape(3, 0, 6, 13, 14, 16)
      )),

      CORNER_LOWER("corner_lower", VoxelShapes.union(
        Block.createCuboidShape(3, 3, 0, 13, 14, 7),
        Block.createCuboidShape(3, 9, 2, 13, 16, 13)
      )),

      CORNER_UPPER("corner_upper", VoxelShapes.union(
        Block.createCuboidShape(3, 2, 0, 13, 13, 7),
        Block.createCuboidShape(3, 0, 2, 13, 7, 13)
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
