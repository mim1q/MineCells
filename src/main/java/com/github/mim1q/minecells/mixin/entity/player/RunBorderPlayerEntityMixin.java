package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.dimension.MineCellsDimensions;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class RunBorderPlayerEntityMixin extends RunBorderEntityMixin {
  @Shadow public abstract boolean isSpectator();

  private Vec3d getCenter() {
    try {
      if (age > 10 && !(isSpectator()) && MineCellsDimensions.isMineCellsDimension(this.getWorld())) {
        return Vec3d.ofCenter(MathUtils.getClosestMultiplePosition(this.getBlockPos(), 1024));
      }
    } catch (NullPointerException e) {
      return null;
    }
    return null;
  }

  @Override
  protected double minecells$modifySetPosX(double x) {
    var center = getCenter();
    if (center == null) {
      return x;
    }
    var dx = x - center.getX();
    if (Math.abs(dx) > 511) {
      stopRiding();
      return center.getX() + MathHelper.sign(dx) * 510.99;
    }
    return x;
  }

  @Override
  protected double minecells$modifySetPosZ(double z) {
    var center = getCenter();
    if (center == null) {
      return z;
    }
    var dz = z - center.getZ();
    if (Math.abs(dz) > 511) {
      stopRiding();
      return center.getZ() + MathHelper.sign(dz) * 510.99;
    }
    return z;
  }
}
