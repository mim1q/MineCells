package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.accessor.EntityAccessor;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Arm;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor, Nameable, EntityLike, CommandOutput {
  @Shadow public abstract Vec3d getPos();

  @Shadow public abstract float getBodyYaw();

  @Shadow public abstract boolean isPlayer();

  @Accessor("dimensions")
  public abstract void setDimensions(EntityDimensions dimensions);

  @Inject(method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V", at = @At("HEAD"), cancellable = true)
  @SuppressWarnings("ConstantConditions")
  private void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo ci) {
    if (passenger instanceof TentacleWeaponEntity && this.isPlayer()) {
      double zOffset = ((PlayerEntity) (Object) this).getMainArm() == Arm.RIGHT ? -0.35D : 0.35D;
      Vec3d pos = this.getPos();
      float yaw = this.getBodyYaw();
      Vec3d offset = MathUtils.vectorRotateY(new Vec3d(0.0D, 1.0D, zOffset), MathUtils.radians(yaw));
      positionUpdater.accept(passenger, pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);
      ci.cancel();
    }
  }
}
