package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class ConjunctiviusObeliskEntity extends ObeliskEntity {
  public ConjunctiviusObeliskEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public Item getActivationItem() {
    return Items.DIAMOND;
  }

  @Override
  public EntityType<?> getEntityType() {
    return MineCellsEntities.CONJUNCTIVIUS;
  }

  @Override
  public Box getBox() {
    return this.getBoundingBox().expand(28.0D);
  }

  @Override
  protected void spawnEntity() {
    ConjunctiviusEntity boss = (ConjunctiviusEntity) getEntityType().create(this.getWorld());
    if (boss == null) {
      return;
    }
    boss.setPos(this.getX(), this.getY() + 10, this.getZ());
    boss.initialize((ServerWorldAccess) this.getWorld(), this.world.getLocalDifficulty(this.getBlockPos()), SpawnReason.SPAWNER, null, null);
    this.world.spawnEntity(boss);
  }
}
