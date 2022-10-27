package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpikesBlock extends Block {
  private static final VoxelShape SHAPE_BOTTOM = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
  private static final VoxelShape SHAPE_TOP = Block.createCuboidShape(0, 14, 0, 16, 16, 16);
  private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0, 0, 14, 16, 16, 16);

  public static final DirectionProperty FACING = DirectionProperty.of("facing");
  public static final BooleanProperty BLOODY = BooleanProperty.of("bloody");

  public SpikesBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(BLOODY, false));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, BLOODY);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(FACING, ctx.getSide());
  }

  @Override
  @SuppressWarnings("deprecation")
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    if (entity instanceof LivingEntity livingEntity) {
      livingEntity.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.BLEEDING, 201, 1, false, false, true));
      livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1, false, false, true));
      livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1, false, false, true));
      if (livingEntity.age % 10 == 0) {
        livingEntity.damage(MineCellsDamageSource.BLEEDING, 1.0f);
      }
      if (world.getRandom().nextFloat() < 0.01) {
        world.setBlockState(pos, state.with(BLOODY, true));
      }
    }
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    Direction dir = state.get(FACING);
    return switch (dir) {
      case DOWN -> SHAPE_TOP;
      case UP -> SHAPE_BOTTOM;
      default -> ModelUtils.rotateShape(Direction.NORTH, dir, SHAPE_NORTH);
    };
  }
}
