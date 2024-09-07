package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class ConciergeObeliskEntity extends BossObeliskEntity {
  private static final Identifier SPAWNER_RUNE = MineCells.createId("boss/concierge");

  public ConciergeObeliskEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public Item getActivationItem() {
    return MineCellsItems.CONCIERGE_RESPAWN_RUNE;
  }

  @Override
  public Identifier getSpawnerRuneDataId() {
    return SPAWNER_RUNE;
  }

  @Override
  public Box getBox() {
    return Box.of(getPos(), 256, 256, 256);
  }

  @Override
  protected void postProcessEntity(Entity entity) {
    var pos = getPos().add(getRotationVector().multiply(-5.0));
    entity.refreshPositionAndAngles(pos.x, pos.y + 0.5, pos.z, 0F, 0F);
    ((MobEntity) entity).initialize((ServerWorldAccess) this.getWorld(), getWorld().getLocalDifficulty(this.getBlockPos()), SpawnReason.NATURAL, null, null);
  }
}
