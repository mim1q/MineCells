package com.github.mim1q.minecells.datagen.util;

import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;

public interface DatagenAdvancementUtils extends DatagenUtils {
  default void addAdvancement(Identifier id, Advancement.Builder advancement) {
    getInitializers().advancements().add(advancement.build(id));
  }
}
