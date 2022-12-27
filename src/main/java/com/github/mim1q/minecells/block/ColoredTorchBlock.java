package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.ColoredTorchBlockEntity;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
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

public class ColoredTorchBlock extends BlockWithEntity {
  public static final VoxelShape SHAPE = VoxelShapes.union(
    createCuboidShape(7.0D, 3.0D, 1.0D, 9.0D, 11.0D, 3.0D),
    createCuboidShape(6.0D, 11.0D, 0.0D, 10.0D, 14.0D, 4.0D)
  );
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private final Identifier flameTexture;

  public ColoredTorchBlock(Settings settings, String flameType) {
    super(settings);
    this.flameTexture = MineCells.createId("textures/blockentity/metal_torch/" + flameType + ".png");
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getSide() == Direction.UP || ctx.getSide() == Direction.DOWN) {
      return null;
    }
    BlockPos pos = ctx.getBlockPos().add(ctx.getSide().getOpposite().getVector());
    if (ctx.getWorld().getBlockState(pos).isSideSolidFullSquare(ctx.getWorld(), pos, ctx.getSide())) {
      return getDefaultState().with(FACING, ctx.getSide());
    }
    return null;
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    Direction facing = state.get(FACING);
    if (direction == facing.getOpposite() && !neighborState.isSideSolidFullSquare(world, pos, facing)) {
      return Blocks.AIR.getDefaultState();
    }
    return state;
  }

  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new ColoredTorchBlockEntity(pos, state);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.SOUTH, state.get(FACING), SHAPE);
  }

  public Identifier getFlameTexture() {
    return this.flameTexture;
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
