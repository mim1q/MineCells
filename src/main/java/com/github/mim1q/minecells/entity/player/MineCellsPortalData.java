package com.github.mim1q.minecells.entity.player;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
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
//    player.setLastDimensionTranslationKey(MineCellsDimension.getTranslationKey(dimension));
  }

  public Pair<String, BlockPos> pop() {
    if (portalStack.isEmpty()) {
      return null;
    }
    Pair<String, BlockPos> pair = portalStack.pop();
    String translationKey = "dimension.minecraft.overworld";
    if (!portalStack.isEmpty()) {
      translationKey = MineCellsDimension.getTranslationKey(portalStack.peek().getLeft());
    }
//    player.setLastDimensionTranslationKey(translationKey);
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
    int i = 0;
    for (Pair<String, BlockPos> pair : portalStack) {
      NbtCompound subNbt = new NbtCompound();
      subNbt.putString("dimensionKey", pair.getLeft());
      subNbt.putLong("position", pair.getRight().asLong());
      nbt.put(String.valueOf(i), subNbt);
      i++;
    }
    return nbt;
  }

  public void fromNbt(NbtCompound nbt) {
    portalStack.clear();
    Set<String> keys = nbt.getKeys();
    for (int i = 0; i < keys.size(); i++) {
      NbtCompound element = nbt.getCompound(String.valueOf(i));
      if (element == null || element.isEmpty()) {
        continue;
      }
      portalStack.add(new Pair<>(
        element.getString("dimensionKey"),
        BlockPos.fromLong(element.getLong("position"))
      ));
    }
    if (portalStack.isEmpty()) {
      return;
    }
//    player.setLastDimensionTranslationKey(MineCellsDimension.getTranslationKey(peek().getLeft()));
  }
}
