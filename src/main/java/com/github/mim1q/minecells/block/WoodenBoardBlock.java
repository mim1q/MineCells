package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WoodenBoardBlock extends Block {
  public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

  public static final VoxelShape SHAPE = createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);

  public WoodenBoardBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(TYPE, FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    Direction facing = ctx.getPlayerFacing().getOpposite();
    BlockState stateBelow = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
    if (ctx.getSide() == Direction.UP && stateBelow.isOf(this)) {
      facing = stateBelow.get(FACING);
    }
    BlockState stateAbove = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
    if (ctx.getSide() == Direction.DOWN && stateAbove.isOf(this)) {
      facing = stateAbove.get(FACING);
    }
    return getDefaultState().with(FACING, facing);
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (player.getStackInHand(hand).getItem() instanceof AxeItem) {
      world.setBlockState(pos, state.cycle(TYPE));
      return ActionResult.SUCCESS;
    }
    return ActionResult.PASS;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.SOUTH, state.get(FACING), SHAPE);
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  public enum Type implements StringIdentifiable {
    MIDDLE,
    BOARDED,
    TOP,
    TOP_INVERTED,
    BOTTOM,
    BOTTOM_INVERTED;

    @Override
    public String asString() {
      return this.name().toLowerCase();
    }
  }
}
