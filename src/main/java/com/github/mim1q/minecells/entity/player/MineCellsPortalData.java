package com.github.mim1q.minecells.entity.player;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Set;
import java.util.Stack;

public class MineCellsPortalData {
  private final Stack<Pair<String, BlockPos>> portalStack = new Stack<>();

  public void push(RegistryKey<World> dimension, BlockPos pos) {
    portalStack.push(new Pair<>(dimension.getValue().toString(), pos));
  }

  public Pair<String, BlockPos> pop() {
    return portalStack.pop();
  }

  public Pair<String, BlockPos> peek() {
    return portalStack.peek();
  }

  public NbtCompound toNbt() {
    NbtCompound nbt = new NbtCompound();
    for (Pair<String, BlockPos> pair : portalStack) {
      nbt.putLong(pair.getLeft(), pair.getRight().asLong());
    }
    return nbt;
  }

  public void fromNbt(NbtCompound nbt) {
    portalStack.clear();
    Set<String> keys = nbt.getKeys();
    for (String key : keys) {
      long element = nbt.getLong(key);
      portalStack.add(new Pair<>(key, BlockPos.fromLong(element)));
    }
  }
}
