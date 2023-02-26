package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class DecorativeStatueBlock extends Block implements BlockEntityProvider {
  public static final IntProperty ROTATION = Properties.ROTATION;

  public DecorativeStatueBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(ROTATION, MathHelper.floor((double)(ctx.getPlayerYaw() * 16.0F / 360.0F) + 0.5) & 15);
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

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(ROTATION);
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
}
