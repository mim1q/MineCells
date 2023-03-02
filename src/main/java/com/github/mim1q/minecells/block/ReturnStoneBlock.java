package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.ReturnStoneBlockEntity;
import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ReturnStoneBlock extends Block implements BlockEntityProvider {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final VoxelShape SHAPE = createCuboidShape(3.0F, 0.0F, 3.0F, 13.0F, 16.0F, 13.0F);
  public static final ParticleEffect PARTICLE = ColoredParticle.create(MineCellsParticles.SPECKLE, 0xFFC410);

  public ReturnStoneBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    ParticleUtils.addParticle(
      (ClientWorld) world,
      PARTICLE,
      Vec3d.ofCenter(pos).add(0.0D, 0.75D, 0.0D),
      Vec3d.fromPolar(random.nextFloat() * 360.0F, random.nextFloat() * 360.0F).multiply(0.03D)
    );
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new ReturnStoneBlockEntity(pos, state);
  }
}
