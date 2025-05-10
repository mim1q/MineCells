package com.github.mim1q.minecells.datagen.util;

import net.minecraft.block.Block;

public interface DatagenLootTableUtils extends DatagenUtils {
  default void addSimpleDrop(Block block) {
    getInitializers().blockLootTable().add(it -> it.addDrop(block));
  }

  default void addSimpleDrop(Block... blocks) {
    getInitializers().blockLootTable().add(it -> {
      for (var block : blocks) it.addDrop(block);
    });
  }
}
