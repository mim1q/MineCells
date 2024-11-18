package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class MineCellsBossEntity extends MineCellsEntity {
  protected final ServerBossBar bossBar;

  protected MineCellsBossEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);
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
    PlayerLookup.tracking(this).forEach(player -> {
      var diff = this.getPos().subtract(player.getPos()).normalize();
      var packet = new PlaySoundS2CPacket(
        RegistryEntry.of(sound),
        SoundCategory.HOSTILE,
        player.getX() + diff.getX(), player.getY() + diff.getY(), player.getZ() + diff.getZ(),
        volume, pitch, getRandom().nextLong()
      );
      player.networkHandler.sendPacket(packet);
    });
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
  protected void dropXp() {
    if (!getWorld().isClient) {
      this.bossBar.getPlayers().forEach(player -> {
        var item = new ItemEntity(getWorld(), getX(), getY(), getZ(), MineCellsItems.BOSS_STEM_CELL.getDefaultStack());
        item.setOwner(player.getUuid());
        item.setVelocity(Vec3d.ZERO.addRandom(getRandom(), 0.3f));
        getWorld().spawnEntity(item);
      });
    }
    super.dropXp();
  }

  @Override
  protected boolean isDisallowedInPeaceful() {
    return false;
  }
}
