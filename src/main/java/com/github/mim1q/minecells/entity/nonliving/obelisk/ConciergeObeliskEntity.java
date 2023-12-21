package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.util.math.Box;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class ConciergeObeliskEntity extends BossObeliskEntity {
  public ConciergeObeliskEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public Item getActivationItem() {
    return MineCellsItems.CONCIERGE_RESPAWN_RUNE;
  }

  @Override
  public EntityType<?> getEntityType() {
    return MineCellsEntities.CONCIERGE;
  }

  @Override
  public Box getBox() {
    return Box.of(getPos(), 128, 128, 128);
  }

  @Override
  protected void spawnEntity() {
    var boss = new ConciergeEntity(MineCellsEntities.CONCIERGE, getWorld());
    var pos = getPos().add(getRotationVector().multiply(-5.0));
    boss.refreshPositionAndAngles(pos.x, pos.y + 0.5, pos.z, 0F, 0F);
    boss.initialize((ServerWorldAccess) this.getWorld(), getWorld().getLocalDifficulty(this.getBlockPos()), SpawnReason.NATURAL, null, null);
    getWorld().spawnEntity(boss);
  }
}
