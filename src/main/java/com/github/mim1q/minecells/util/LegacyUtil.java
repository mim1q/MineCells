package com.github.mim1q.minecells.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

// Utilities for porting to 1.21
// Methods will be deprecated once I'm ready to properly port to new system
public class LegacyUtil {
  public static NbtCompound getOrCreateNbt(ItemStack stack) {
    var nbt = stack.get(DataComponentTypes.CUSTOM_DATA);
    if (nbt == null) return new NbtCompound();

    return nbt.copyNbt();
  }

  public static NbtCompound getOrCreateSubNbt(ItemStack stack, String key) {
    var nbt = getOrCreateNbt(stack);
    if (nbt.contains(key)) return nbt.getCompound(key);
    var newNbt = new NbtCompound();
    nbt.put(key, newNbt);
    return newNbt;
  }
}
