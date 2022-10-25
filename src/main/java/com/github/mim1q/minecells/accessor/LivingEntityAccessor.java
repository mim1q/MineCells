package com.github.mim1q.minecells.accessor;

import com.github.mim1q.minecells.effect.MineCellsEffectFlags;

public interface LivingEntityAccessor {
  boolean getMineCellsFlag(MineCellsEffectFlags flag);

  void setMineCellsFlag(MineCellsEffectFlags flag, boolean value);
}
