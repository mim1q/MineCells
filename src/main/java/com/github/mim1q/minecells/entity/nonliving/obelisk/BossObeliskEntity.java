package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.List;

public abstract class BossObeliskEntity extends ObeliskEntity {
  public BossObeliskEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  protected void serverTick() {
    super.serverTick();
    if (this.age % 20 == 0) {
      List<PlayerEntity> players = this.getWorld().getPlayers(TargetPredicate.DEFAULT, null, this.getBox());
      for (PlayerEntity player : players) {
        player.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.DISARMED, 175, 0, false, false, true));
      }
    }
  }
}
