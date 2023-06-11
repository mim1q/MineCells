package com.github.mim1q.minecells.world.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class PlayerSpecificMineCellsData {
  public final ImmutableMap<String, MineCellsData.PlayerData> map;

  public PlayerSpecificMineCellsData (MineCellsData data, ServerPlayerEntity player) {
    var builder = new ImmutableMap.Builder<String, MineCellsData.PlayerData>();
    data.runs.forEach((k, v) -> builder.put(v.x + "," + v.z, v.getPlayerData(player)));
    map = builder.build();
  }

  public MineCellsData.PlayerData get(BlockPos pos) {
    return get(Math.round(pos.getX() / 1024F), Math.round(pos.getZ() / 1024F));
  }

  public MineCellsData.PlayerData get(int x, int z) {
    var key = x + "," + z;
    if (!map.containsKey(key)) {
      return MineCellsData.PlayerData.EMPTY;
    }
    return map.get(key);
  }

  public PlayerSpecificMineCellsData(NbtCompound nbt) {
    var builder = new ImmutableMap.Builder<String, MineCellsData.PlayerData>();
    nbt.getKeys().forEach(k -> builder.put(k, new MineCellsData.PlayerData(nbt.getCompound(k), null)));
    map = builder.build();
  }

  public NbtCompound toNbt() {
    var nbt = new NbtCompound();
    map.forEach((k, v) -> nbt.put(k, v.writeNbt(new NbtCompound())));
    return nbt;
  }
}
