package com.github.mim1q.minecells.accessor;

import com.github.mim1q.minecells.effect.MineCellsEffectFlags;

public interface LivingEntityAccessor {
  boolean getMineCellsFlag(MineCellsEffectFlags flag);
  void setMineCellsFlag(MineCellsEffectFlags flag, boolean value);
  void mixinSetCellAmountAndChance(int amount, float chance);
  void clearCurableStatusEffects();
  boolean hasIncurableStatusEffects();
  default boolean shouldActFrozen() {
    return getMineCellsFlag(MineCellsEffectFlags.FROZEN) || getMineCellsFlag(MineCellsEffectFlags.STUNNED);
  }
}
