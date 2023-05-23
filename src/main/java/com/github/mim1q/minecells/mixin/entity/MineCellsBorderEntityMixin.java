package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.accessor.MineCellsBorderEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimensions;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.entity.EntityLike;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class MineCellsBorderEntityMixin implements Nameable, EntityLike, CommandOutput, MineCellsBorderEntity {
  @Shadow public int age;
  @Shadow public abstract BlockPos getBlockPos();
  @Shadow public World world;
  @Shadow public abstract void stopRiding();

  private final WorldBorder minecellsBorder = new WorldBorder();

  @Override
  public WorldBorder getMineCellsBorder() {
    return minecellsBorder;
  }

  @Inject(method = "tick", at = @At("HEAD"))
  public void minecells$updateBorderOnTick(CallbackInfo ci) {
    if (MineCellsDimensions.isMineCellsDimension(world)) {
      if (age % 20 == 0) {
        var pos = MathUtils.getClosestMultiplePosition(getBlockPos(), 1024);
        minecellsBorder.setCenter(pos.getX() + 0.5D, pos.getZ() + 0.5D);
        minecellsBorder.setSize(1023);
      }
      if (minecellsBorder.getDistanceInsideBorder((Entity)(Object)this) <= 2.0) {
        stopRiding();
      }
    }
  }

  // IntelliJ believes this won't compile, but it does.
  @ModifyVariable(
    method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
    at = @At("STORE")
  )
  private static WorldBorder minecells$modifyWorldBorderWhenAdjustingMovement(
    WorldBorder original,
    @Nullable Entity entity,
    Vec3d movement,
    Box entityBoundingBox,
    World world,
    List<VoxelShape> collisions
  ) {
    if (entity != null && MineCellsDimensions.isMineCellsDimension(world)) {
      return ((MineCellsBorderEntityMixin)(Object)entity).minecellsBorder;
    }
    return original;
  }

  @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
  public void minecells$cancelStartRiding(Entity entity, CallbackInfoReturnable<Boolean> cir) {
    if (minecellsBorder.getDistanceInsideBorder(entity) < 2.0D) {
      cir.setReturnValue(false);
    }
  }
}
