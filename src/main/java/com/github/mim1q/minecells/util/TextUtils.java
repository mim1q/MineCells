package com.github.mim1q.minecells.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

public class TextUtils {
  public static void addDescription(List<Text> tooltip, String key, Object... params) {
    tooltip.addAll(Arrays.stream(Text.translatable(key, params).getString().split("\n"))
      .map(str -> Text.literal(str).formatted(Formatting.GRAY))
      .toList()
    );
  }
}
