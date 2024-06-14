package com.github.mim1q.minecells.client.gui;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

public class CustomBossBarRenderer {
  private static final Identifier TEXTURE = MineCells.createId("textures/gui/boss_bars.png");
  private static final String CONJUNCTIVIUS_KEY = "entity.minecells.conjunctivius";
  private static final String CONCIERGE_KEY = "entity.minecells.concierge";
  private static final int TEXTURE_SIZE = 256;

  public static boolean renderCustomBossBar(DrawContext context, int x, int y, BossBar bossBar) {
    if (bossBar.getName().getContent() instanceof TranslatableTextContent translatable) {
      var key = translatable.getKey();
      if (!key.startsWith("entity.minecells.")) return false;

      return switch (key) {
        case CONJUNCTIVIUS_KEY -> renderBossBar(context, x, y, bossBar, 0);
        case CONCIERGE_KEY -> renderBossBar(context, x, y, bossBar, 1);
        default -> false;
      };
    }

    return false;
  }

  private static boolean renderBossBar(DrawContext context, int x, int y, BossBar bossBar, int verticalIndex) {
    var u = 0;
    var v = verticalIndex * 64;

    context.drawTexture(TEXTURE, x, y, u, v, 208, 32, TEXTURE_SIZE, TEXTURE_SIZE);
    var width = 14 + (int) (180 * bossBar.getPercent());
    context.drawTexture(TEXTURE, x, y, u, v + 32, width, 32, TEXTURE_SIZE, TEXTURE_SIZE);

    return true;
  }

  public static Text getCustomBossBarName(Text text) {
    if (text.getContent() instanceof TranslatableTextContent translatable) {
      var key = translatable.getKey();
      if (!key.startsWith("entity.minecells.")) return text;

      return switch (key) {
        case CONJUNCTIVIUS_KEY -> text.copy().styled(it -> it.withBold(true).withColor(0xDF5FE2));
        case CONCIERGE_KEY -> text.copy().styled(it -> it.withBold(true).withColor(0xF8af0C));
        default -> text;
      };
    }
    return text;
  }
}
