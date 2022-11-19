package com.github.mim1q.minecells.block.blockentity.spawnerrune;

import net.minecraft.nbt.NbtCompound;

public class SpawnerRuneData {
  public final String name;
  public final EntryList entryList;
  public final int maxCooldown;
  public final int minRolls;
  public final int maxRolls;
  public final float spawnRadius;
  public final float playerRange;

  public SpawnerRuneData(String name, EntryList entryList, int maxCooldown, int minRolls, int maxRolls, float spawnRadius, float playerRange) {
    this.name = name;
    this.entryList = entryList;
    this.maxCooldown = maxCooldown;
    this.minRolls = minRolls;
    this.maxRolls = maxRolls;
    this.spawnRadius = spawnRadius;
    this.playerRange = playerRange;
  }

  public static SpawnerRuneData fromNbt(NbtCompound nbt) {
    return new SpawnerRuneData(
      nbt.getString("name"),
      EntryList.fromNbt(nbt.getList("entryList", 10)),
      nbt.getInt("maxCooldown"),
      nbt.getInt("minRolls"),
      nbt.getInt("maxRolls"),
      nbt.getFloat("spawnRadius"),
      nbt.getFloat("playerRange")
    );
  }

  public void writeNbt(NbtCompound nbt) {
    nbt.putString("name", name);
    nbt.put("entryList", entryList.toNbt());
    nbt.putInt("maxCooldown", maxCooldown);
    nbt.putInt("minRolls", minRolls);
    nbt.putInt("maxRolls", maxRolls);
    nbt.putFloat("spawnRadius", spawnRadius);
    nbt.putFloat("playerRange", playerRange);
  }
}
