package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SpawnerRuneEntity extends Entity {
  private Identifier dataId = MineCells.createId("unknown");
  private SpawnerRuneData data = null;
  private final SpawnerRuneController controller = new SpawnerRuneController();

  public SpawnerRuneEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public void tick() {
    if (age % 10 == 0) {
      controller.tick(data, getBlockPos(), world);
    }
  }

  public boolean isVisible() {
    return controller.isVisible();
  }

  @Override
  protected void initDataTracker() {
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    setDataId(Identifier.tryParse(nbt.getString("dataId")));
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putString("dataId", dataId.toString());
  }

  public void setDataId(Identifier id) {
    if (world.isClient) return;
    var newData = MineCells.SPAWNER_RUNE_DATA.get(id);
    if (newData == null) {
      MineCells.LOGGER.warn("Tried to load unknown spawner rune data with id: " + id
        + " at pos " + getBlockPos().toShortString()
        + " in dimension " + world.getRegistryKey().getValue().toString()
      );
      return;
    }
    this.dataId = id;
    this.data = MineCells.SPAWNER_RUNE_DATA.get(id);
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }
}
