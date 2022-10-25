package com.github.mim1q.minecells.world.state;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;

public class OverworldPortals extends PersistentState {

  private int portalsCount = 0;
  private final List<BlockPos> portals = new ArrayList<>();

  public OverworldPortals() {

  }

  public OverworldPortals(NbtCompound nbt) {
    portalsCount = nbt.getInt("portalsCount");
    for (int i = 0; i < portalsCount; i++) {
      portals.add(BlockPos.fromLong(nbt.getLong("portal" + i)));
    }
  }

  public static OverworldPortals get(ServerWorld world) {
    var overworld = world.getServer().getOverworld();
    return overworld.getPersistentStateManager().getOrCreate(
      OverworldPortals::new,
      OverworldPortals::new,
      "overworld_portals"
    );
  }

  public BlockPos getPortalPos(int portalId) {
    if (portalsCount == 0) {
      return null;
    }
    if (portalId >= portalsCount) {
      return portals.get(portalsCount - 1);
    }
    return portals.get(portalId);
  }

  public int addPortal(BlockPos pos) {
    MineCells.LOGGER.info("Adding portal at " + pos + " with id " + portalsCount);
    portals.add(pos);
    portalsCount++;
    markDirty();
    return portalsCount - 1;
  }

  public int getPortalsCount() {
    return portalsCount;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    nbt.putInt("portalsCount", portalsCount);
    for (int i = 0; i < portalsCount; i++) {
      nbt.putLong("portal" + i, portals.get(i).asLong());
    }
    return nbt;
  }
}
