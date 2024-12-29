package com.github.mim1q.minecells.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

public interface FallResetEntity {
  void minecells$initDimensionChange(Entity result, ServerWorld destination);
}
