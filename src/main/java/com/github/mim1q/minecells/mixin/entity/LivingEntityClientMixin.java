package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityClientMixin extends Entity {
  @Shadow public abstract Random getRandom();

  protected LivingEntityClientMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void minecells$spawnEffectParticles(CallbackInfo ci) {
    if (!getWorld().isClient || this.getRandom().nextFloat() < 0.2F) {
      return;
    }
    LivingEntityAccessor accessor = ((LivingEntityAccessor)this);
    ParticleEffect particle = null;

    if (accessor.getMineCellsFlag(MineCellsEffectFlags.FROZEN)) {
      particle = ParticleTypes.SNOWFLAKE;
    }

    if (particle != null) {
      ParticleUtils.addInBox(
        (ClientWorld) getWorld(),
        particle,
        this.getBoundingBox().expand(0.1D),
        1,
        new Vec3d(0.001D, 0.001D, 0.001D)
      );
    }
  }
}
