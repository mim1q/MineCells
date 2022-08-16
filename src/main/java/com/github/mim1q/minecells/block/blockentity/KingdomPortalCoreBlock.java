package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.BlockEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class KingdomPortalCoreBlock extends Block implements BlockEntityProvider {
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

  public static class Filler extends FillerBlock {
    public Filler(Settings settings) {
      super(settings, BlockEntityRegistry.KINGDOM_PORTAL_CORE, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return VoxelShapes.fullCube();
    }
  }
}
