package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public abstract class MineCellsBossEntity extends MineCellsEntity {
  protected final ServerBossBar bossBar;

  protected MineCellsBossEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);
    this.setCellAmountAndChance(25, 1.0F);
  }

  @Override
  public void tick() {
    super.tick();
    if (!getWorld().isClient()) {
      this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }
  }

  @Override
  public void playSound(SoundEvent sound, float volume, float pitch) {
    if (getWorld().isClient) {
      getWorld().playSound(getX(), getY(), getZ(), sound, SoundCategory.HOSTILE, volume, pitch, false);
      return;
    }
    for (ServerPlayerEntity player : this.bossBar.getPlayers()) {
      var packet = new PlaySoundS2CPacket(
        RegistryEntry.of(sound),
        SoundCategory.HOSTILE, player.getX(), player.getY(), player.getZ(), volume, pitch, getRandom().nextLong()
      );
      player.networkHandler.sendPacket(packet);
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

  @Override
  protected boolean isDisallowedInPeaceful() {
    return false;
  }
}
