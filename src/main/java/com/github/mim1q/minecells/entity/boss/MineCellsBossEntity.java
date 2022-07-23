package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class MineCellsBossEntity extends MineCellsEntity {
  protected final ServerBossBar bossBar;

  protected MineCellsBossEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);
    this.droppedCellAmount = 25;
    this.droppedCellChance = 1.0F;
  }

  @Override
  public void tick() {
    super.tick();
    if (!this.world.isClient()) {
      this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }
  }

  @Override
  public boolean cannotDespawn() {
    return true;
  }

  public void onStartedTrackingBy(ServerPlayerEntity player) {
    this.bossBar.addPlayer(player);
  }

  public void onStoppedTrackingBy(ServerPlayerEntity player) {
    this.bossBar.removePlayer(player);
  }
}
