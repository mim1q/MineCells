package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.BlockEntityRegistry;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class KingdomPortalCoreBlock extends BlockWithEntity {

  public static final DirectionProperty DIRECTION = HorizontalFacingBlock.FACING;

  public KingdomPortalCoreBlock(Settings settings) {
    super(settings);
  }

  @SuppressWarnings("deprecation")
  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity == null) {
      return ActionResult.FAIL;
    }
    if (world.isClient()) {
      return ActionResult.SUCCESS;
    }
    System.out.println("onUse");
    return super.onUse(state, world, pos, player, hand, hit);
  }

  @Override
  public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
    FillerBlock.destroyNeighbors(world, pos, BlockEntityRegistry.KINGDOM_PORTAL_FILLER, this);
    super.onBroken(world, pos, state);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new KingdomPortalCoreBlockEntity(pos, state);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return VoxelShapes.empty();
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(DIRECTION);
  }

  public static class Filler extends FillerBlock {
    public static final EnumProperty<Part> PART = EnumProperty.of("part", Part.class);
    public static final DirectionProperty DIRECTION = HorizontalFacingBlock.FACING;

    private static final VoxelShape BOTTOM_SHAPE = VoxelShapes.union(
      Block.createCuboidShape(3, 0, 0, 13, 10, 13),
      Block.createCuboidShape(3, 3, 11, 13, 14, 16)
    );

    private static final VoxelShape TOP_SHAPE = VoxelShapes.union(
      Block.createCuboidShape(3, 6, 0, 13, 16, 13),
      Block.createCuboidShape(3, 2, 11, 13, 13, 16)
    );

    private static final VoxelShape SIDE_LOWER_SHAPE = VoxelShapes.union(
      Block.createCuboidShape(3, 0, 2, 13, 4, 13),
      Block.createCuboidShape(3, 2, 6, 13, 16, 16)
    );

    private static final VoxelShape SIDE_UPPER_SHAPE = VoxelShapes.union(
      Block.createCuboidShape(3, 12, 2, 13, 16, 13),
      Block.createCuboidShape(3, 0, 6, 13, 14, 16)
    );

    private static final VoxelShape CORNER_LOWER_SHAPE = VoxelShapes.union(
      Block.createCuboidShape(3, 3, 0, 13, 14, 7),
      Block.createCuboidShape(3, 9, 2, 13, 16, 13)
    );

    private static final VoxelShape CORNER_UPPER_SHAPE = VoxelShapes.union(
      Block.createCuboidShape(3, 2, 0, 13, 13, 7),
      Block.createCuboidShape(3, 0, 2, 13, 7, 13)
    );

    public Filler(Settings settings) {
      super(settings, BlockEntityRegistry.KINGDOM_PORTAL_CORE, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(PART, DIRECTION);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      VoxelShape shape = switch (state.get(PART)) {
        case BOTTOM -> BOTTOM_SHAPE;
        case TOP -> TOP_SHAPE;
        case SIDE_LOWER -> SIDE_LOWER_SHAPE;
        case SIDE_UPPER -> SIDE_UPPER_SHAPE;
        case CORNER_LOWER -> CORNER_LOWER_SHAPE;
        case CORNER_UPPER -> CORNER_UPPER_SHAPE;
        case MIDDLE -> VoxelShapes.empty();
      };

      return ModelUtils.rotateShape(Direction.NORTH, state.get(DIRECTION), shape);
    }

    public enum Part implements StringIdentifiable {
      BOTTOM("bottom"),
      TOP("top"),
      CORNER_LOWER("corner_lower"),
      CORNER_UPPER("corner_upper"),
      SIDE_LOWER("side_lower"),
      SIDE_UPPER("side_upper"),
      MIDDLE("middle");

      private final String name;

      Part(String name) {
        this.name = name;
      }

      @Override
      public String asString() {
        return name;
      }
    }
  }
}
