package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.accessor.MineCellsBorderEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class BorderEntityCollisionMixin {

  @Inject(
    method = "findCollisionsForMovement",
    at = @At("TAIL"),
    require = 0,
    cancellable = true
  )
  private static void minecells$modifyWorldBorderWhenAdjustingMovement(
    Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox, CallbackInfoReturnable<List<VoxelShape>> cir, @Local(ordinal = 0) ImmutableList.Builder<VoxelShape> builder
  ) {
    if (entity != null && MineCellsDimension.isMineCellsDimension(world)) {
      var border = ((MineCellsBorderEntity) entity).getMineCellsBorder();
      var shape = VoxelShapes.cuboid(
        border.getBoundWest(), Double.NEGATIVE_INFINITY, border.getBoundNorth(),
        border.getBoundEast(), Double.POSITIVE_INFINITY, border.getBoundSouth()
      );
      builder.add(VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, shape, BooleanBiFunction.ONLY_FIRST));
      cir.setReturnValue(builder.build());
    }
  }
}
