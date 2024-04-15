package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ElectrifiedStatusEffect extends StatusEffect {
  public ElectrifiedStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0xCCF4FF);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    if (entity.isDead()) return;

    var damage = 1.0F;
    var interval = 15;
    if (entity.isInsideWaterOrBubbleColumn()) {
      damage *= 1.25F;
      interval = 10;
    }

    if (entity.age % interval != 0) return;

    entity.damage(MineCellsDamageSource.ELECTRICITY.get(entity.getWorld()), damage);
    entity.getWorld().playSound(null, entity.getBlockPos(), MineCellsSounds.SHOCK, SoundCategory.NEUTRAL, 0.5F, 0.8F + entity.getRandom().nextFloat() * 0.4F);

    var serverWorld = (ServerWorld) entity.getWorld();

    for (int i = 0; i < 5; ++i) {
      serverWorld.spawnParticles(
        MineCellsParticles.ELECTRICITY.get(Vec3d.ZERO.addRandom(entity.getRandom(), 1.0f), 2, 0xFFFFFF, 0.3F),
        entity.getX(),
        entity.getY() + entity.getHeight() / 2.0D,
        entity.getZ(),
        1,
        entity.getWidth() / 4.0,
        entity.getHeight() / 4.0,
        entity.getWidth() / 4.0,
        0.0D
      );
    }
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    if ((amplifier == 1 && entity.isInsideWaterOrBubbleColumn()) || amplifier >= 2) {

      var entities = entity.getWorld().getOtherEntities(
        entity,
        Box.of(entity.getPos(), 10.0, 10.0, 10.0),
        e -> e instanceof LivingEntity && !e.isPlayer()
      );

      var effect = new StatusEffectInstance(
        MineCellsStatusEffects.ELECTRIFIED,
        20 * 3 + 1,
        amplifier - 1,
        false,
        false,
        true
      );

      for (Entity e : entities) {
        if (
          e instanceof LivingEntity livingEntity
            && (e.distanceTo(entity) <= 3.0D)
            && e.getWorld() instanceof ServerWorld serverWorld
        ) {

          var currentEffect = livingEntity.getStatusEffect(MineCellsStatusEffects.ELECTRIFIED);
          if (currentEffect == null) {
            livingEntity.addStatusEffect(effect);
          } else {
            currentEffect.upgrade(effect);
          }

          var particlePos = entity.getPos().add(0, entity.getHeight() / 2.0D, 0);
          var targetPos = e.getPos().add(0, e.getHeight() / 2.0D, 0);
          var direction = targetPos.subtract(particlePos);
          var length = (int) (particlePos.distanceTo(targetPos) * 2);

          serverWorld.spawnParticles(
            MineCellsParticles.ELECTRICITY.get(direction, length, 0xBBEEFF, 0.5F),
            particlePos.getX(),
            particlePos.getY(),
            particlePos.getZ(),
            1,
            0.0D,
            0.0D,
            0.0D,
            0.0D
          );
        }
      }
    }
  }
}
