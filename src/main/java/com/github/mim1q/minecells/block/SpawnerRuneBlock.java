package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.spawnerrune.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpawnerRuneBlock extends BlockWithEntity implements BlockEntityProvider {

  public static final VoxelShape SHAPE = createCuboidShape(4, 0, 4, 12, 0.1, 12);

  public SpawnerRuneBlock(Settings settings) {
    super(settings);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new SpawnerRuneBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return world.isClient ? null : checkType(type, MineCellsBlockEntities.SPAWNER_RUNE_BLOCK_ENTITY, SpawnerRuneBlockEntity::tick);
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    for (int i = 0; i < 3; i++) {
      Random rng = world.getRandom();
      Vec3d particlePos = Vec3d.of(pos).add(rng.nextFloat(), 0.0F, rng.nextFloat());
      float offset = rng.nextFloat() * 0.02F;
      world.addParticle(
        ColoredParticle.create(MineCellsParticles.SPECKLE, 0xFF8000),
        particlePos.getX(), particlePos.getY() - offset, particlePos.getZ(),
        0.0D, 0.01D + rng.nextFloat() * 0.01D + offset, 0.0D
      );
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }
}
