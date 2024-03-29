package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.ArrowSignBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.floorMod;
import static java.lang.Math.round;

public class ArrowSignBlock extends BlockWithEntity {
  public static final IntProperty ROTATION = Properties.ROTATION;
  public static final BooleanProperty MIDDLE = BooleanProperty.of("middle");

  public ArrowSignBlock(Settings settings) {
    super(settings);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    var direction = ctx.getSide();
    if (direction.getAxis().isHorizontal()) {
      return getDefaultState().with(ROTATION, direction.getHorizontal() * 4).with(MIDDLE, false);
    }
    var rotation = floorMod(round(ctx.getPlayerYaw() / 22.5f) - 8, 16);
    return getDefaultState().with(ROTATION, rotation).with(MIDDLE, true);
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (world.isClient) {
      return ActionResult.SUCCESS;
    }
    var blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof ArrowSignBlockEntity arrowSignBlockEntity) {
      if (arrowSignBlockEntity.getItemStack().isEmpty()) {
        var itemStack = player.getStackInHand(hand);
        if (!itemStack.isEmpty()) {
          arrowSignBlockEntity.setItemStack(itemStack.copy());
          return ActionResult.SUCCESS;
        }
      }
      arrowSignBlockEntity.cycleVerticalRotation((player.isSneaking()) ? -1 : 1);
    }
    return ActionResult.SUCCESS;
  }

  @Override
  public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    super.onPlaced(world, pos, state, placer, itemStack);
    updateChainState(world, pos, state);
  }

  @Override
  @SuppressWarnings("deprecation")
  public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    updateChainState(world, pos, state);
    if (sourcePos.equals(pos.up())) {
      world.updateNeighbor(pos.down(), this, pos);
    }
  }

  private static void updateChainState(World world, BlockPos pos, BlockState state) {
    if (world.isClient || !state.get(MIDDLE)) {
      return;
    }
    var blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof ArrowSignBlockEntity arrowSignBlockEntity) {
      var stateAbove = world.getBlockState(pos.up());
      var blockEntityAbove = world.getBlockEntity(pos.up());
      if (blockEntityAbove instanceof ArrowSignBlockEntity arrowSignBlockEntityAbove) {
        stateAbove = arrowSignBlockEntityAbove.getChainState();
      }

      var stateBelow = world.getBlockState(pos.down());

      var chainAbove = stateAbove.getBlock() instanceof ChainBlock && stateAbove.get(ChainBlock.AXIS) == Axis.Y;
      var chainBelow = stateBelow.getBlock() instanceof ChainBlock && stateBelow.get(ChainBlock.AXIS) == Axis.Y;
      var signBelow = stateBelow.getBlock() instanceof ArrowSignBlock && stateBelow.get(MIDDLE);

      if (signBelow || chainBelow) {
        arrowSignBlockEntity.setChainState(chainAbove ? stateAbove : Blocks.CHAIN.getDefaultState());
        return;
      }
      arrowSignBlockEntity.setChainState(Blocks.AIR.getDefaultState());
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(ROTATION, floorMod(state.get(ROTATION) + rotation.ordinal() * 4, 16));
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new ArrowSignBlockEntity(pos, state);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(ROTATION, MIDDLE);
  }
}
