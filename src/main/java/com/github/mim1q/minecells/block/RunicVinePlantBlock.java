package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.RunicVinePlantBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class RunicVinePlantBlock extends BlockWithEntity {
  public static final ParticleEffect PARTICLE = MineCellsParticles.SPECKLE.get(0x49b74a);

  public RunicVinePlantBlock(Settings settings) {
    super(settings);
  }

  public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(ACTIVATED);
    super.appendProperties(builder);
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    var entity = world.getBlockEntity(pos);
    if (entity instanceof RunicVinePlantBlockEntity blockEntity) {
      return blockEntity.use(state, world, pos, player, hand);
    }
    return ActionResult.FAIL;
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (direction == Direction.UP) {
      if (!neighborState.isOf(MineCellsBlocks.RUNIC_VINE)) {
        return state.with(ACTIVATED, false);
      }
    }
    return state;
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
    var particlePos = Vec3d.of(pos).add(random.nextDouble(), random.nextDouble(), random.nextDouble());
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
