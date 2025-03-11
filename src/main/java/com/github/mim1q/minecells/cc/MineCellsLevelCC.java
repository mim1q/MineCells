package com.github.mim1q.minecells.cc;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.util.MathUtils;
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
import net.minecraft.util.math.BlockPos;

import java.util.*;


public class MineCellsLevelCC implements ScoreboardComponentInitializer {
  public static final ComponentKey<PortalsCC> PORTALS = ComponentRegistry.getOrCreate(MineCells.createId("portals"), PortalsCC.class);

  @Override
  public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
    registry.registerScoreboardComponent(PORTALS, PortalsCC::new);
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
      return portalMap.containsKey(player.getUuid());
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
      var list = portalMap.get(recipient.getUuid());
      buf.writeVarInt(list.size());
      for (var portal : list) {
        buf.writeNbt(portal.createNbt());
      }
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
      var size = buf.readVarInt();
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

    public static Optional<PortalData> findDataOfPosition(ServerWorld world, BlockPos pos) {
      return PORTALS.get(world.getScoreboard()).portals
        .stream()
        .filter(p -> p.runCenter.equals(pos))
        .findFirst();
    }

    public static void visitDimension(ServerWorld world, BlockPos posOverride, MineCellsDimension dimension) {
      var data = findDataOfPosition(world, posOverride);
      data.ifPresent(it -> it.visitedDimensions.add(dimension));

      PORTALS.sync(world.getScoreboard());
    }
  }

  public record PortalData(
    UUID owner,
    BlockPos runCenter,
    EnumSet<MineCellsDimension> visitedDimensions
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
      return new PortalData(
        UUID.fromString(nbt.getString("owner")),
        BlockPos.fromLong(nbt.getLong("run_center")),
        set
      );
    }
  }
}
