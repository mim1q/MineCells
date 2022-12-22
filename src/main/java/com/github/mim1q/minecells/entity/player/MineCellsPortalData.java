package com.github.mim1q.minecells.entity.player;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.dimension.MineCellsDimensions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Set;
import java.util.Stack;

public class MineCellsPortalData {
  private final PlayerEntityAccessor player;
  private final Stack<Pair<String, BlockPos>> portalStack = new Stack<>();

  public MineCellsPortalData(PlayerEntityAccessor player) {
    this.player = player;
  }

  public void push(RegistryKey<World> dimension, BlockPos pos) {
    portalStack.push(new Pair<>(dimension.getValue().toString(), pos));
    player.setLastDimensionTranslationKey(MineCellsDimensions.getTranslationKey(dimension));
  }

  public Pair<String, BlockPos> pop() {
    if (portalStack.isEmpty()) {
      return null;
    }
    Pair<String, BlockPos> pair = portalStack.pop();
    String translationKey = "dimension.minecraft.overworld";
    if (!portalStack.isEmpty()) {
      translationKey = MineCellsDimensions.getTranslationKey(portalStack.peek().getLeft());
    }
    player.setLastDimensionTranslationKey(translationKey);
    return pair;
  }

  public Pair<String, BlockPos> peek() {
    if (portalStack.isEmpty()) {
      return null;
    }
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
    if (portalStack.isEmpty()) {
      return;
    }
    player.setLastDimensionTranslationKey(MineCellsDimensions.getTranslationKey(peek().getLeft()));
  }
}
