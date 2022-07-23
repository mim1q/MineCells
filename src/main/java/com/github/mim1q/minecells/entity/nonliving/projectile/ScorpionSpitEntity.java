package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class ScorpionSpitEntity extends MagicOrbEntity {

  private static final ParticleEffect PARTICLE = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.getDefaultState());

  public ScorpionSpitEntity(EntityType<ScorpionSpitEntity> entityType, World world) {
    super(entityType, world);
    this.noClip = false;
  }

  @Override
  public void tick() {
    super.tick();
    if (!this.world.isClient() && this.horizontalCollision || this.verticalCollision) {
      this.kill();
    }
  }

  @Override
  protected void onEntityHit(EntityHitResult entityHitResult) {
    Entity entity = entityHitResult.getEntity();

    if (entity instanceof PlayerEntity playerEntity) {
      DamageSource damageSource = this.getOwner() == null
        ? DamageSource.MAGIC
        : DamageSource.mob((LivingEntity) this.getOwner()
      );
      entity.damage(damageSource, 8.0F);
      playerEntity.addStatusEffect(new StatusEffectInstance(
        StatusEffects.POISON,
        60 + 20 * (this.world.getDifficulty().getId() - 1),
        this.world.getDifficulty() == Difficulty.HARD ? 1 : 0
      ));
      this.kill();
    }
  }

  @Override
  protected void spawnParticles() {
    ParticleUtils.addParticle((ClientWorld) this.world, PARTICLE, this.getPos().add(0.0F, 0.25F, 0.0F), Vec3d.ZERO);
  }
}
