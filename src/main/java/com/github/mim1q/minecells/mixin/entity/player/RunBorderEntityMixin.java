package com.github.mim1q.minecells.mixin.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class RunBorderEntityMixin {
  @Shadow public abstract World getWorld();
  @Shadow public abstract Vec3d getPos();
  @Shadow public abstract BlockPos getBlockPos();
  @Shadow public abstract double getX();
  @Shadow public abstract double getZ();

  @Shadow public abstract void stopRiding();

  @ModifyVariable(method = "setPos", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
  protected double minecells$modifySetPosX(double x) { return x; }

  @ModifyVariable(method = "setPos", at = @At(value = "HEAD"), ordinal = 2, argsOnly = true)
  protected double minecells$modifySetPosZ(double z) { return z; }
}
