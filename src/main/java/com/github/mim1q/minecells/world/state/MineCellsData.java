package com.github.mim1q.minecells.world.state;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.network.s2c.SyncMineCellsPlayerDataS2CPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.*;

public class MineCellsData extends PersistentState {
  public final Map<Integer, RunData> runs = new HashMap<>();
  private PlayerEntity wipeScheduledPlayer = null;
  private String lastMineCellsVersion = null;
  private String mineCellsVersion = null;

  public void wipe(ServerWorld world, ServerPlayerEntity player) {
    if (player.hasPermissionLevel(2)) {
      if (wipeScheduledPlayer == player) {
        runs.clear();
        markDirty();
        world.getPlayers().forEach(p -> {
          syncCurrentPlayerData(p, world);
          MineCells.DIMENSION_GRAPH.saveStuckPlayer(p);
        });
        wipeScheduledPlayer = null;
        player.sendMessage(Text.translatable("chat.minecells.wipe_success"));
      } else {
        wipeScheduledPlayer = player;
        player.sendMessage(Text.translatable("chat.minecells.wipe_try"));
      }
    }
  }

  public static MineCellsData fromNbt(NbtCompound nbt) {
    var data = new MineCellsData();
    if (nbt.contains("mineCellsVersion")) {
      data.lastMineCellsVersion = nbt.getString("mineCellsVersion");
    }
    FabricLoader.getInstance().getModContainer(MineCells.MOD_ID).ifPresent(
      mod -> {
        var fullVersion = mod.getMetadata().getVersion().getFriendlyString();
        data.mineCellsVersion = fullVersion.substring(0, fullVersion.lastIndexOf('.'));
      }
    );
    if (nbt.contains("runs")) {
      var entryNbt = nbt.getCompound("runs");
      for (var id : entryNbt.getKeys()) {
        data.runs.put(data.runs.size(), new RunData(entryNbt.getCompound(id), data));
      }
    }
    return data;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    var entryNbt = new NbtCompound();
    for (var entry : runs.entrySet()) {
      entryNbt.put(entry.getKey().toString(), entry.getValue().writeNbt(new NbtCompound()));
    }
    nbt.put("runs", entryNbt);
    if (mineCellsVersion != null) {
      nbt.putString("mineCellsVersion", mineCellsVersion);
    }
    return nbt;
  }

  public void wipeIfVersionMismatched(ServerWorld world) {
    if (
      lastMineCellsVersion != null
      && mineCellsVersion != null
      && !mineCellsVersion.equals(lastMineCellsVersion)
      && MineCells.COMMON_CONFIG.autoWipeData
    ) {
      MineCells.LOGGER.warn("Mine Cells version changed from " + lastMineCellsVersion + " to " + mineCellsVersion + "!");
      MineCells.LOGGER.warn("Mine Cells data will be wiped!");
      runs.clear();
      lastMineCellsVersion = mineCellsVersion;
      markDirty();
      world.getPlayers().forEach(p -> {
        syncCurrentPlayerData(p, world);
        MineCells.DIMENSION_GRAPH.saveStuckPlayer(p);
        if (p.hasPermissionLevel(2) || world.getServer().isSingleplayer()) {
          p.sendMessage(Text.translatable("chat.minecells.wipe_success"));
        }
      });
    }
  }

  public static MineCellsData get(ServerWorld world) {
    return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(MineCellsData::fromNbt, MineCellsData::new, "MineCellsData");
  }

  public static PlayerData getPlayerData(ServerPlayerEntity player, ServerWorld world, BlockPos posOverride) {
    return get(world).getRun(posOverride == null ? player.getBlockPos() : posOverride).getPlayerData(player);
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

  public static void syncCurrentPlayerData(ServerPlayerEntity player, ServerWorld world) {
    var data = new PlayerSpecificMineCellsData(get(world), player);
    ((PlayerEntityAccessor)player).setMineCellsData(data);
    SyncMineCellsPlayerDataS2CPacket.send(player, data);
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
    public static final PlayerData EMPTY = new PlayerData(new NbtCompound(), null);

    public final Map<MineCellsDimension, List<BlockPos>> activatedSpawnerRunes = new HashMap<>();
    public final List<PortalData> portals = new ArrayList<>();
    private final PersistentState parent;

    public PlayerData(NbtCompound nbt, PersistentState parent) {
      this.parent = parent;
      var runeNbt = nbt.getCompound("ActivatedSpawnerRunes");
      for (var id : runeNbt.getKeys()) {
        var dimension = MineCellsDimension.of(new Identifier(id));
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
      if (parent != null) {
        parent.markDirty();
      }
    }

    public boolean hasActivatedSpawnerRune(MineCellsDimension dimension, BlockPos pos) {
      return activatedSpawnerRunes.computeIfAbsent(dimension, k -> new ArrayList<>()).contains(pos);
    }

    public boolean hasVisitedDimension(MineCellsDimension dimension) {
      return portals.stream().anyMatch(it -> it.toDimension == dimension || it.fromDimension == dimension);
    }

    public void addPortalData(
      MineCellsDimension fromDimension,
      MineCellsDimension toDimension,
      BlockPos fromPos,
      BlockPos toPos
    ) {
      portals.removeIf(data ->
        (data.fromDimension == fromDimension && data.toDimension == toDimension)
        || (data.toDimension == toDimension && data.fromDimension == toDimension)
      );
      portals.add(new PortalData(fromDimension, toDimension, fromPos, toPos));
      if (parent != null) {
        parent.markDirty();
      }
    }

    public Optional<PortalData> getPortalData(MineCellsDimension fromDimension, MineCellsDimension toDimension) {
      var result = portals.stream()
        .filter(it -> it.fromDimension == fromDimension && it.toDimension == toDimension)
        .findFirst();
      if (result.isPresent()) {
        return result;
      }
      return portals.stream()
        .filter(it -> it.fromDimension == toDimension && it.toDimension == fromDimension)
        .findFirst()
        .map(it -> new PortalData(it.toDimension, it.fromDimension, it.toPos, it.fromPos));
    }

    public record PortalData(
      MineCellsDimension fromDimension,
      MineCellsDimension toDimension,
      BlockPos fromPos,
      BlockPos toPos
    ) {
      public static PortalData fromNbt(NbtCompound nbt) {
        return new PortalData(
          MineCellsDimension.of(new Identifier(nbt.getString("FromDimension"))),
          MineCellsDimension.of(new Identifier(nbt.getString("ToDimension"))),
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
