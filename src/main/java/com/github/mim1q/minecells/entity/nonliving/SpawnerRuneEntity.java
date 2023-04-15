package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class SpawnerRuneEntity extends Entity {
  private Identifier dataId = MineCells.createId("prison");
  private SpawnerRuneData data = MineCells.SPAWNER_RUNE_DATA.get(dataId);

  public SpawnerRuneEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public void tick() {
    if (!world.isClient && age % 200 == 0 && data != null) {
      if (world.getClosestPlayer(this, data.playerDistance()) != null) {
        spawnEntities();
      }
    }
  }

  private void spawnEntities() {
    var entities = data.getSelectedEntities(random);
    for (var entity : entities) {
      var e = entity.create(world);
      if (e == null) continue;
      var dx = 2.0F * (random.nextFloat() - 0.5F) * data.spawnDistance().get(random);
      var dz = 2.0F * (random.nextFloat() - 0.5F) * data.spawnDistance().get(random);
      e.setPosition(getX() + dx, getY(), getZ() + dz);
      if (e instanceof HostileEntity hostile) {
        hostile.initialize((ServerWorldAccess) world, world.getLocalDifficulty(e.getBlockPos()), SpawnReason.NATURAL, null, null);
      }
      world.spawnEntity(e);
    }
  }

  @Override
  protected void initDataTracker() { }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    setDataId(Identifier.tryParse(nbt.getString("dataId")));
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putString("dataId", dataId.toString());
  }

  public void setDataId(Identifier id) {
    this.dataId = id;
    this.data = MineCells.SPAWNER_RUNE_DATA.get(id);
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }
}
