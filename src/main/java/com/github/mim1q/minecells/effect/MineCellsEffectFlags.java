package com.github.mim1q.minecells.effect;

public enum MineCellsEffectFlags {
  PROTECTED(0),
  BLEEDING(1),
  AWAKENED(2),
  CURSED(3),
  DISARMED(4),
  FROZEN(5),
  STUNNED(6);

  private final int offset;

  MineCellsEffectFlags(int bit) {
    this.offset = 1 << bit;
  }

  public int getOffset() {
    return offset;
  }
}
