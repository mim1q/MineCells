package com.github.mim1q.minecells.valuecalculators;

import com.github.mim1q.minecells.MineCells;
import dev.mim1q.gimm1q.valuecalculators.ValueCalculator;

public class ModValueCalculators {

  public static void init() {

  }

  public static ValueCalculator of(String id, String name, double fallback) {
    return ValueCalculator.of(MineCells.createId(id), name, fallback);
  }
}
