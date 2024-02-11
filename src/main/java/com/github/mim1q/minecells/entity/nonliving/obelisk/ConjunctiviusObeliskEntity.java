package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class ConjunctiviusObeliskEntity extends BossObeliskEntity {
  private static final Identifier SPAWNER_RUNE = MineCells.createId("boss/conjunctivius");

  public ConjunctiviusObeliskEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public Item getActivationItem() {
    return MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE;
  }

  @Override
  public Identifier getSpawnerRuneDataId() {
    return SPAWNER_RUNE;
  }


  @Override
  public Box getBox() {
    return new Box(this.getX() - 13, this.getY() - 1, this.getZ() - 25, this.getX() + 13, this.getY() + 25, this.getZ() + 5);
  }

  @Override
  protected void postProcessEntity(Entity entity) {
    entity.refreshPositionAndAngles(this.getX(), this.getY() + 10, this.getZ(), 180F, 0F);
    ((ConjunctiviusEntity) entity).initialize((ServerWorldAccess) this.getWorld(), getWorld().getLocalDifficulty(this.getBlockPos()), SpawnReason.SPAWNER, null, null);
  }

  @Override
  protected void spawnActivationParticles(int activatedTicks) {
    ParticleEffect particle = MineCellsParticles.SPECKLE.get(0xFF0000);
    for (int i = 0; i < activatedTicks; i++) {
      float yOff = this.random.nextFloat() * 10.0F;
      float xOff = this.random.nextFloat() - 0.5F;
      float zOff = this.random.nextFloat() - 0.5F;
      ParticleUtils.addParticle((ClientWorld) getWorld(), particle, this.getPos().add(xOff, yOff, zOff), new Vec3d(0.0D, 0.2D, 0.0D));
    }
    if (activatedTicks >= 38) {
      ParticleUtils.addParticle((ClientWorld) getWorld(), ParticleTypes.EXPLOSION_EMITTER, this.getPos().add(0.0D, 12.5D, 0.0D), Vec3d.ZERO);
    }
  }
}
