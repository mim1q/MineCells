package com.github.mim1q.minecells.cc;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.util.MathUtils;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;


public class MineCellsLevelCC implements ScoreboardComponentInitializer {
  public static final ComponentKey<PortalsCC> PORTALS = ComponentRegistry.getOrCreate(MineCells.createId("portals"), PortalsCC.class);
  public static final ComponentKey<OverworldEntriesCC> OVERWORLD_ENTRIES = ComponentRegistry.getOrCreate(MineCells.createId("overworld_entries"), OverworldEntriesCC.class);

  @Override
  public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
    registry.registerScoreboardComponent(PORTALS, PortalsCC::new);
    registry.registerScoreboardComponent(OVERWORLD_ENTRIES, (sb, sv) -> new OverworldEntriesCC());
  }

  public static class PortalsCC implements AutoSyncedComponent {
    private final ArrayList<PortalData> portals = new ArrayList<>();
    private final HashMap<UUID, List<PortalData>> portalMap = new HashMap<>();
    private final Scoreboard provider;

    public PortalsCC(Scoreboard provider, MinecraftServer server) {
      this.provider = provider;
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
      portals.clear();
      portalMap.clear();

      var list = nbt.getList("portals", NbtCompound.COMPOUND_TYPE);
      for (var compound : list) {
        addPortal(PortalData.fromNbt((NbtCompound) compound));
      }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
      var list = new NbtList();
      for (var portal : portals) {
        list.add(portal.createNbt());
      }
      nbt.put("portals", list);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
      return true;//portalMap.containsKey(player.getUuid());
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
      var list = portalMap.get(recipient.getUuid());
      if (list == null) return;

      buf.writeInt(list.size());
      for (var portal : list) {
        buf.writeNbt(portal.createNbt());
      }
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
      portals.clear();
      portalMap.clear();

      var size = buf.readInt();
      for (int i = 0; i < size; i++) {
        var portalNbt = buf.readNbt();
        if (portalNbt == null) continue;
        addPortal(PortalData.fromNbt(portalNbt));
      }
    }

    private Optional<PortalData> createPortalDataForPlayer(ServerPlayerEntity player) {
      var maxPortals = 1;
      var list = portalMap.get(player.getUuid());

      if (list != null && list.size() > maxPortals) {
        return Optional.empty();
      }

      var index = portals.size();
      var pos = MathUtils.getSpiralPosition(index).multiply(1024);
      var portal = new PortalData(player.getUuid(), new BlockPos(pos), EnumSet.noneOf(MineCellsDimension.class));
      addPortal(portal);

      PORTALS.sync(provider);

      return Optional.of(portal);
    }

    private void addPortal(PortalData portal) {
      portals.add(portal);
      portalMap.computeIfAbsent(portal.owner, k -> new ArrayList<>()).add(portal);
    }

    public static List<PortalData> getPortals(ServerPlayerEntity player) {
      return PORTALS.get(player.server.getScoreboard()).portalMap.get(player.getUuid());
    }

    public static boolean createPortal(ServerPlayerEntity player) {
      var component = PORTALS.get(player.server.getScoreboard());
      return component.createPortalDataForPlayer(player).isPresent();
    }

    public static PortalData getOrCreatePortal(ServerPlayerEntity player) {
      var component = PORTALS.get(player.server.getScoreboard());
      var list = component.portalMap.get(player.getUuid());
      if (list == null || list.isEmpty()) {
        return component.createPortalDataForPlayer(player).get();
      }

      return list.get(0);
    }

    public static Optional<PortalData> findDataOfPosition(World world, BlockPos pos) {
      var centerPos = new BlockPos(MathUtils.getClosestMultiplePosition(pos, 1024));
      return PORTALS.get(world.getScoreboard()).portals.stream().filter(p -> p.runCenter.equals(centerPos)).findFirst();
    }

    public static void visitDimension(ServerWorld world, BlockPos posOverride, MineCellsDimension dimension) {
      var data = findDataOfPosition(world, posOverride);
      data.ifPresent(it -> it.visitedDimensions.add(dimension));

      PORTALS.sync(world.getScoreboard());
    }
  }

  public record PortalData(
    UUID owner, BlockPos runCenter, EnumSet<MineCellsDimension> visitedDimensions
  ) {
    public NbtCompound createNbt() {
      var nbt = new NbtCompound();
      nbt.putString("owner", owner.toString());
      nbt.putLong("run_center", runCenter.asLong());
      var list = new NbtList();
      for (var dimension : visitedDimensions) {
        list.add(NbtString.of(dimension.key.getValue().toString()));
      }
      nbt.put("visited_dimensions", list);
      return nbt;
    }

    public static PortalData fromNbt(NbtCompound nbt) {
      var set = EnumSet.noneOf(MineCellsDimension.class);
      var dimensions = nbt.getList("visited_dimensions", NbtElement.STRING_TYPE);
      for (var dim : dimensions) {
        set.add(MineCellsDimension.of(Identifier.tryParse(dim.asString())));
      }
      return new PortalData(UUID.fromString(nbt.getString("owner")), BlockPos.fromLong(nbt.getLong("run_center")), set);
    }
  }

  public static class OverworldEntriesCC implements Component {
    private final HashMap<UUID, Data> entries = new HashMap<>();

    public static Optional<Data> getPlayerEntrancePoint(ServerPlayerEntity player, BlockPos pos, ServerWorld world) {
      var entries = OVERWORLD_ENTRIES.get(world.getScoreboard());
      var entry = entries.entries.get(player.getUuid());
      if (entry == null || !entry.entrancePos.equals(pos)) return Optional.empty();

      return Optional.of(entry);
    }

    public static void setPlayerEntrancePoint(
      ServerPlayerEntity player,
      BlockPos portalPos,
      BlockPos entrancePos,
      float entranceRot,
      ServerWorld world
    ) {
      var entries = OVERWORLD_ENTRIES.get(world.getScoreboard());
      entries.entries.put(player.getUuid(), new Data(portalPos, entrancePos, entranceRot));
    }

    public static Pair<BlockPos, Float> getPlayerEntrancePos(
      ServerPlayerEntity player
    ) {
      var world = player.getServerWorld();
      var entries = OVERWORLD_ENTRIES.get(world.getScoreboard());

      var entry = entries.entries.get(player.getUuid());
      if (entry != null) {
        return new Pair<>(entry.entrancePos, entry.entranceRotation);
      }

      if (player.getSpawnPointDimension() == MineCellsDimension.OVERWORLD.key) {
        var spawnPoint = player.getSpawnPointPosition();
        var spawnRot = player.getSpawnAngle();
        if (spawnPoint != null) return new Pair<>(spawnPoint, spawnRot);
      }

      return new Pair<>(world.getServer().getOverworld().getSpawnPos(), world.getServer().getOverworld().getSpawnAngle());

    }

    @Override
    public void readFromNbt(NbtCompound tag) {
      entries.clear();
      for (var key : tag.getKeys()) {
        var data = tag.getLongArray(key);
        entries.put(
          UUID.fromString(key),
          new Data(
            BlockPos.fromLong(data[0]),
            BlockPos.fromLong(data[1]),
            (float) data[2]
          )
        );
      }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
      entries.forEach((k, v) -> {
        tag.putLongArray(k.toString(), new long[]{v.posOverride.asLong(), v.entrancePos.asLong(), (long) v.entranceRotation});
      });
    }

    public record Data(BlockPos posOverride, BlockPos entrancePos, float entranceRotation) {
    }
  }
}
