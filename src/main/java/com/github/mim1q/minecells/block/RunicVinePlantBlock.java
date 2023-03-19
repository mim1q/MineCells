package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.RunicVinePlantBlockEntity;
import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RunicVinePlantBlock extends BlockWithEntity {
  private static final ParticleEffect PARTICLE = ColoredParticle.create(MineCellsParticles.SPECKLE, 0x49b74a);

  public RunicVinePlantBlock(Settings settings) {
    super(settings);
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    var entity = world.getBlockEntity(pos);
    if (entity instanceof RunicVinePlantBlockEntity blockEntity) {
      blockEntity.usedTicks = 20;
      return ActionResult.SUCCESS;
    }
    return ActionResult.FAIL;
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.ENTITYBLOCK_ANIMATED;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return VoxelShapes.empty();
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    Vec3d particlePos = Vec3d.of(pos).add(random.nextDouble(), random.nextDouble(), random.nextDouble());
    world.addParticle(PARTICLE, particlePos.getX(), particlePos.getY(), particlePos.getZ(), 0.0D, 0.01D + random.nextDouble() * 0.03D, 0.0D);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new RunicVinePlantBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return checkType(type, MineCellsBlockEntities.RUNIC_VINE_PLANT, RunicVinePlantBlockEntity::tick);
  }
}
