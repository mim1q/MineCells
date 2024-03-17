package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DecorativeStatueBlock extends Block implements BlockEntityProvider {
  public static final IntProperty ROTATION = Properties.ROTATION;
  public static final EnumProperty<StatuePose> POSE = EnumProperty.of("pose", StatuePose.class);

  public DecorativeStatueBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0).with(POSE, StatuePose.ARM_UP_LEFT));
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(ROTATION, MathHelper.floor((double) (ctx.getPlayerYaw() * 16.0F / 360.0F) + 0.5) & 15);
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(ROTATION, rotation.rotate(state.get(ROTATION), 16));
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState mirror(BlockState state, BlockMirror mirror) {
    return state.with(ROTATION, mirror.mirror(state.get(ROTATION), 16));
  }

  @SuppressWarnings("deprecation")
  @Override public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (!world.isClient) {
      world.setBlockState(pos, state.cycle(POSE));
    }
    return ActionResult.SUCCESS;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(ROTATION, POSE);
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new DecorativeStatueBlockEntity(pos, state);
  }

  public enum StatuePose implements StringIdentifiable {
    BASE("base"),
    ARM_UP_RIGHT("arm_up_right"),
    ARM_UP_LEFT("arm_up_left"),
    ARMS_UP("arms_up");

    private final String name;

    StatuePose(String name) {
      this.name = name;
    }

    @Override
    public String asString() {
      return this.name;
    }
  }
}
