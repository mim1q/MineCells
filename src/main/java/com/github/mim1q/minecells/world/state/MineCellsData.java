package com.github.mim1q.minecells.world.state;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.*;

public class MineCellsData extends PersistentState {
  private final Map<Integer, RunData> runs = new HashMap<>();

  @Override
  public void markDirty() {
    super.markDirty();
    System.out.println(writeNbt(new NbtCompound()));
  }

  public static MineCellsData fromNbt(NbtCompound nbt) {
    var data = new MineCellsData();
    for (var id : nbt.getKeys()) {
      data.runs.put(Integer.parseInt(id), new RunData(nbt.getCompound(id), data));
    }
    return data;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    for (var entry : runs.entrySet()) {
      nbt.put(entry.getKey().toString(), entry.getValue().writeNbt(new NbtCompound()));
    }
    return nbt;
  }

  public static MineCellsData get(ServerWorld world) {
    return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(MineCellsData::fromNbt, MineCellsData::new, "MineCellsData");
  }

  public static PlayerData getPlayerData(PlayerEntity player, ServerWorld world) {
    return get(world).getRun(player.getBlockPos()).getPlayerData(player);
  }

  public RunData getRun(int x, int z) {
    for (var entry : runs.entrySet()) {
      if (entry.getValue().x == x && entry.getValue().z == z) {
        return entry.getValue();
      }
    }
    var run = new RunData(x, z, this);
    runs.put(runs.size(), run);
    markDirty();
    return run;
  }

  public RunData getRun(BlockPos pos) {
    return getRun(Math.round(pos.getX() / 1024F), Math.round(pos.getZ() / 1024F));
  }

  public static class RunData {
    public final int x;
    public final int z;
    private final PersistentState parent;

    public final Map<UUID, PlayerData> players = new HashMap<>();

    public RunData(int x, int z, PersistentState parent) {
      this.x = x;
      this.z = z;
      this.parent = parent;
    }

    public RunData(NbtCompound nbt, PersistentState parent) {
      this.x = nbt.getInt("X");
      this.z = nbt.getInt("Z");
      var playersNbt = nbt.getCompound("Players");
      for (var uuid : playersNbt.getKeys()) {
        players.put(UUID.fromString(uuid), new PlayerData(playersNbt.getCompound(uuid), parent));
      }
      this.parent = parent;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
      nbt.putInt("X", x);
      nbt.putInt("Z", z);
      var playersNbt = new NbtCompound();
      for (var entry : players.entrySet()) {
        var compound = new NbtCompound();
        playersNbt.put(entry.getKey().toString(), entry.getValue().writeNbt(compound));
      }
      nbt.put("Players", playersNbt);
      return nbt;
    }

    public PlayerData getPlayerData(PlayerEntity player) {
      if (players.containsKey(player.getUuid())) {
        return players.get(player.getUuid());
      }
      var playerData = new PlayerData(new NbtCompound(), parent);
      players.put(player.getUuid(), playerData);
      parent.markDirty();
      return playerData;
    }
  }

  public static class PlayerData {
    public final Map<MineCellsDimension, List<BlockPos>> activatedSpawnerRunes = new HashMap<>();
    public final List<PortalData> portals = new ArrayList<>();
    private final PersistentState parent;

    public PlayerData(NbtCompound nbt, PersistentState parent) {
      this.parent = parent;
      var runeNbt = nbt.getCompound("ActivatedSpawnerRunes");
      for (var id : runeNbt.getKeys()) {
        var dimension = MineCellsDimension.getFrom(new Identifier(id));
        activatedSpawnerRunes.put(dimension, new ArrayList<>());
        for (var pos : runeNbt.getLongArray(id)) {
          activatedSpawnerRunes.get(dimension).add(BlockPos.fromLong(pos));
        }
      }
      var portalNbt = nbt.getCompound("Portals");
      for (var id : portalNbt.getKeys()) {
        portals.add(PortalData.fromNbt(portalNbt.getCompound(id)));
      }
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
      var runeNbt = new NbtCompound();
      for (var id : activatedSpawnerRunes.keySet()) {
        runeNbt.putLongArray(id.key.getValue().toString(), activatedSpawnerRunes.get(id).stream().mapToLong(BlockPos::asLong).toArray());
      }
      nbt.put("ActivatedSpawnerRunes", runeNbt);
      var portalNbt = new NbtCompound();
      int i = 0;
      for (var portal : portals) {
        portalNbt.put(String.valueOf(i), portal.writeNbt(new NbtCompound()));
        i++;
      }
      nbt.put("Portals", portalNbt);
      return nbt;
    }

    public void addActivatedSpawnerRune(MineCellsDimension dimension, BlockPos pos) {
      activatedSpawnerRunes.computeIfAbsent(dimension, k -> new ArrayList<>()).add(pos);
      parent.markDirty();
    }

    public void addPortalData(
      MineCellsDimension fromDimension,
      MineCellsDimension toDimension,
      BlockPos fromPos,
      BlockPos toPos
    ) {
      for (var data : portals) {
        if (data.fromDimension == fromDimension && data.toDimension == toDimension) {
          return;
        }
      }
      portals.add(new PortalData(fromDimension, toDimension, fromPos, toPos));
      parent.markDirty();
    }

    public record PortalData(
      MineCellsDimension fromDimension,
      MineCellsDimension toDimension,
      BlockPos fromPos,
      BlockPos toPos
    ) {
      public static PortalData fromNbt(NbtCompound nbt) {
        return new PortalData(
          MineCellsDimension.getFrom(new Identifier(nbt.getString("FromDimension"))),
          MineCellsDimension.getFrom(new Identifier(nbt.getString("ToDimension"))),
          BlockPos.fromLong(nbt.getLong("FromPos")),
          BlockPos.fromLong(nbt.getLong("ToPos"))
        );
      }

      public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("FromDimension", fromDimension.key.getValue().toString());
        nbt.putString("ToDimension", toDimension.key.getValue().toString());
        nbt.putLong("FromPos", fromPos.asLong());
        nbt.putLong("ToPos", toPos.asLong());
        return nbt;
      }
    }
  }
}
