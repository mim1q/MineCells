package com.github.mim1q.minecells.datagen.util;

import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;

public interface DatagenLootTableUtils extends DatagenUtils {
  default void addSimpleDrop(Block block) {
    getInitializers().blockLootTable().add(it -> it.addDrop(block));
  }

  default void addSimpleDrop(Block... blocks) {
    getInitializers().blockLootTable().add(it -> {
      for (var block : blocks) it.addDrop(block);
    });
  }

  default void addSilkTouchDrop(Block drop, Block dropWithoutSilkTouch) {
    getInitializers().blockLootTable().add(it -> it.addDrop(drop, LootTable.builder()
      .pool(LootPool.builder().with(
          ItemEntry.builder(drop).conditionally(it.createSilkTouchCondition())
            .alternatively(ItemEntry.builder(dropWithoutSilkTouch))
        ).build()
      )
    ));
  }
}
