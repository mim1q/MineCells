package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.BarrierControllerBlockEntity;
import com.github.mim1q.minecells.entity.boss.MineCellsBossEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BarrierControllerBlock extends ConditionalBarrierBlock implements BlockEntityProvider {
  private final BarrierPredicate predicate;

  public BarrierControllerBlock(Settings settings, BarrierPredicate predicate) {
    super(settings);
    this.predicate = predicate;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return MineCellsBlockEntities.BARRIER_CONTROLLER.instantiate(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return type == MineCellsBlockEntities.BARRIER_CONTROLLER
      ? (entityWorld, entityPos, entityState, entity) ->
        ((BarrierControllerBlockEntity)entity).tick(entityWorld, entityPos, entityState)
      : null;
  }

  public boolean shouldBeOpen(World world, BlockPos pos, BlockState state) {
    return predicate.test(world, pos, state);
  }

  @FunctionalInterface
  public interface BarrierPredicate {
    boolean test(World world, BlockPos pos, BlockState state);
  }

  public static boolean bossPredicate(World world, BlockPos pos, BlockState state) {
    var nearestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 4, false);
    if (nearestPlayer == null) return false;
    var noBossAround = world.getEntitiesByClass(
      MineCellsBossEntity.class,
      Box.of(Vec3d.of(pos), 128, 64, 128),
      EntityPredicates.VALID_ENTITY
    ).isEmpty();
    var behindDoorPos = nearestPlayer.getPos()
      .subtract(Vec3d.of(pos))
      .multiply(Vec3d.of(state.get(FACING).getVector()));
    return noBossAround || behindDoorPos.x < 0 || behindDoorPos.z < 0;
  }

  public static boolean bossEntryPredicate(World world, BlockPos pos, BlockState state) {
    return
      (MineCells.COMMON_CONFIG.unlockedBossEntry() && playerPredicate(world, pos, state))
      || bossPredicate(world, pos, state);
  }

  public static boolean playerPredicate(World world, BlockPos pos, BlockState state) {
    return world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 4, false) != null;
  }
}
