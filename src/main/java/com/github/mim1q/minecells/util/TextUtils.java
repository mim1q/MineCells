package com.github.mim1q.minecells.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TextUtils {
  public static void addDescription(List<Text> tooltip, String key, Object... params) {
    var parts = Text.translatable(key, params).getString().split("\n");
    for (var part : parts) {
      tooltip.add(Text.literal(part).formatted(Formatting.GRAY));
    }
  }
}
