package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.accessor.MineCellsBorderEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Entity.class)
public abstract class BorderEntityCollisionMixin {
  @Shadow
  private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
    return null;
  }

  @Inject(
    method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
    at = @At("TAIL"),
    locals = LocalCapture.CAPTURE_FAILHARD,
    require = 0,
    cancellable = true
  )
  private static void minecells$modifyWorldBorderWhenAdjustingMovement(
    @Nullable Entity entity,
    Vec3d movement,
    Box entityBoundingBox,
    World world,
    List<VoxelShape> collisions,
    CallbackInfoReturnable<Vec3d> cir,
    ImmutableList.Builder<VoxelShape> builder
  ) {
    if (entity != null && MineCellsDimension.isMineCellsDimension(world)) {
      var border = ((MineCellsBorderEntity) entity).getMineCellsBorder();
      var shape = VoxelShapes.cuboid(
        border.getBoundWest(), Double.NEGATIVE_INFINITY, border.getBoundNorth(),
        border.getBoundEast(), Double.POSITIVE_INFINITY, border.getBoundSouth()
      );
      builder.add(VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, shape, BooleanBiFunction.ONLY_FIRST));
      cir.setReturnValue(adjustMovementForCollisions(movement, entityBoundingBox, builder.build()));
    }
  }
}
