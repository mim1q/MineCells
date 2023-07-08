package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.block.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BarrierRuneBlock extends BarrierBlock {
  public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");

  private final boolean solid;

  public BarrierRuneBlock(Settings settings, boolean solid) {
    super(settings);
    this.solid = solid;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(BOTTOM);
    super.appendProperties(builder);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
    return false;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    if (solid) return VoxelShapes.fullCube();
    if (
      context instanceof EntityShapeContext entityCtx
      && (entityCtx.getEntity() instanceof HostileEntity || entityCtx.getEntity() instanceof ProjectileEntity)
    ) {
      return VoxelShapes.fullCube();
    }
    return VoxelShapes.empty();
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return context.isHolding(this.asItem()) ? VoxelShapes.fullCube() : VoxelShapes.empty();
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(BOTTOM, !ctx.getWorld().getBlockState(ctx.getBlockPos().down()).isOf(this));
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    if (!state.get(BOTTOM)) {
      return;
    }
    ParticleUtils.addParticle(
      (ClientWorld) world,
      MineCellsParticles.SPECKLE.get(0x00AAEE),
      Vec3d.ofBottomCenter(pos).add(random.nextDouble() - 0.5F, 0.05F, random.nextFloat() - 0.5F),
      new Vec3d(0.0F, 0.01F + random.nextDouble() * 0.05F, 0.0F)
    );
  }
}
