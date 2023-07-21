package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.accessor.MineCellsBorderEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MineCellsBorderEntityMixin implements Nameable, EntityLike, CommandOutput, MineCellsBorderEntity {
  @Shadow public int age;
  @Shadow public abstract BlockPos getBlockPos();
  @Shadow public World world;
  @Shadow public abstract void stopRiding();

  @Unique
  private final WorldBorder minecellsBorder = new WorldBorder();

  @Override
  public WorldBorder getMineCellsBorder() {
    return minecellsBorder;
  }

  @Inject(method = "tick", at = @At("HEAD"))
  public void minecells$updateBorderOnTick(CallbackInfo ci) {
    if (MineCellsDimension.isMineCellsDimension(world)) {
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

  @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
  public void minecells$cancelStartRiding(Entity entity, CallbackInfoReturnable<Boolean> cir) {
    if (minecellsBorder.getDistanceInsideBorder(entity) < 2.0D) {
      cir.setReturnValue(false);
    }
  }
}
