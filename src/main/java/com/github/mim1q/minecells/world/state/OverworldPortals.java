package com.github.mim1q.minecells.world.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.List;

public class OverworldPortals extends PersistentState {

  private int portalsCount = 0;
  private List<BlockPos> portals;

  public BlockPos getPortalPos(int portalId) {
    return portals.get(portalId);
  }

  public void addPortal(BlockPos pos) {
    portals.add(pos);
    portalsCount++;
    markDirty();
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    nbt.putInt("portalsCount", portalsCount);
    for (int i = 0; i < portalsCount; i++) {
      nbt.putLong("portal" + i, portals.get(i).asLong());
    }
    return nbt;
  }

  public void readNbt(NbtCompound nbt) {
    portalsCount = nbt.getInt("portalsCount");
    for (int i = 0; i < portalsCount; i++) {
      portals.add(BlockPos.fromLong(nbt.getLong("portal" + i)));
    }
  }

  public static OverworldPortals get(NbtCompound nbt) {
    OverworldPortals portals = new OverworldPortals();
    portals.readNbt(nbt);
    return portals;
  }
}
