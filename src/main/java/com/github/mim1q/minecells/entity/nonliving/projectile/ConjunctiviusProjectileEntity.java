package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConjunctiviusProjectileEntity extends MagicOrbEntity {
  public ConjunctiviusProjectileEntity(World world, ConjunctiviusEntity owner) {
    super(MineCellsEntities.CONJUNCTIVIUS_PROJECTILE, world, owner);
  }

  public ConjunctiviusProjectileEntity(EntityType<? extends ConjunctiviusProjectileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  public void tick() {
    super.tick();
  }

  public static void spawn(World world, Vec3d pos, Vec3d target, ConjunctiviusEntity owner) {
    var velocity = target.subtract(pos).normalize();
    ConjunctiviusProjectileEntity projectile = new ConjunctiviusProjectileEntity(world, owner);
    projectile.updatePosition(pos.x, pos.y, pos.z);
    projectile.setVelocity(velocity.multiply(1.5D));

    world.spawnEntity(projectile);
  }

  @Override
  public float getDamage() {
    return 8.0f;
  }

  @Override
  protected void spawnParticles() {
    getWorld().addParticle(
      MineCellsParticles.SPECKLE.get(0x00FF40),
      this.getPos().x,
      this.getPos().y,
      this.getPos().z,
      0.0D,
      0.0D,
      0.0D
    );
  }

  @Override
  public void onSpawnPacket(EntitySpawnS2CPacket packet) {
    super.onSpawnPacket(packet);
  }
}
