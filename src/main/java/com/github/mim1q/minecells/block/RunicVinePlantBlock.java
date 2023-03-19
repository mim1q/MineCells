package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.RunicVinePlantBlockEntity;
import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RunicVinePlantBlock extends Block implements BlockEntityProvider {
  private static final ParticleEffect PARTICLE = ColoredParticle.create(MineCellsParticles.SPECKLE, 0x49b74a);

  public RunicVinePlantBlock(Settings settings) {
    super(settings);
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  @Override
  @SuppressWarnings("deprecation")
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
}
