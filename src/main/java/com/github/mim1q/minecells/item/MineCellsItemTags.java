package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class MineCellsItemTags {
  public static final TagKey<Item> DISCARD_IN_HIGH_DIMENSIONS = of("discard_in_high_dimensions");

  private static TagKey<Item> of(String id) {
    return TagKey.of(RegistryKeys.ITEM, MineCells.createId(id));
  }
}
