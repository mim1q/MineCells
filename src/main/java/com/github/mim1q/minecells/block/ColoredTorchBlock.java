package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ColoredTorchBlock extends Block {
  public static final VoxelShape SHAPE = createCuboidShape(6.0D, 3.0D, 0.0D, 10.0D, 14.0D, 4.0D);
  public static final VoxelShape STANDING_SHAPE = createCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 11.0D, 10.0D);
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final BooleanProperty STANDING = BooleanProperty.of("standing");

  public ColoredTorchBlock(Settings settings) {
    super(settings);
    setDefaultState(getDefaultState().with(STANDING, false));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING);
    builder.add(STANDING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getSide() == Direction.DOWN) return null;
    if (ctx.getSide() == Direction.UP) return getDefaultState().with(STANDING, true);
    BlockPos pos = ctx.getBlockPos().add(ctx.getSide().getOpposite().getVector());
    if (ctx.getWorld().getBlockState(pos).isSideSolidFullSquare(ctx.getWorld(), pos, ctx.getSide())) {
      return getDefaultState().with(FACING, ctx.getSide());
    }
    return null;
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (state.get(STANDING)) {
      return direction == Direction.DOWN ? Blocks.AIR.getDefaultState() : state;
    }
    Direction facing = state.get(FACING);
    if (direction == facing.getOpposite() && !neighborState.isSideSolidFullSquare(world, pos, facing)) {
      return Blocks.AIR.getDefaultState();
    }
    return state;
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }
  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    if (state.get(STANDING)) return STANDING_SHAPE;
    return ModelUtils.rotateShape(Direction.SOUTH, state.get(FACING), SHAPE);
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    if (random.nextFloat() > 0.5f) {
      return;
    }

    Vec3d particlePos = getOffsetPos(pos, state.get(FACING));
    world.addParticle(ParticleTypes.SMOKE, particlePos.getX(), particlePos.getY(), particlePos.getZ(), 0.0D, 0.02D, 0.0D);
  }

  public static Vec3d getOffsetPos(BlockPos pos, Direction direction) {
    double x = 0.5D - 0.4D * direction.getOffsetX();
    double z = 0.5D - 0.4D * direction.getOffsetZ();
    Vec3d offset = new Vec3d(x, 1.0D, z);
    return offset.add(pos.getX(), pos.getY(), pos.getZ());
  }
}
