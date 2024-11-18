package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
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
    if (!getWorld().isClient() && this.horizontalCollision || this.verticalCollision) {
      this.kill();
    }
  }

  @Override
  protected void spawnParticles() {
    ParticleUtils.addParticle((ClientWorld) getWorld(), PARTICLE, this.getPos().add(0.0F, 0.25F, 0.0F), Vec3d.ZERO);
  }
}
