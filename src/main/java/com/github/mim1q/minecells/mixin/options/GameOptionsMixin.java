package com.github.mim1q.minecells.mixin.options;

import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.accessor.GameOptionsAccessor;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GameOptions.class)
public class GameOptionsMixin implements GameOptionsAccessor {
  @Unique
  private final SimpleOption<Double> minecellsScreenshake = new SimpleOption<>(
    "options.minecells.screenshake",
    SimpleOption.constantTooltip(Text.translatable("options.minecells.screenshake.tooltip")),
    GameOptionsMixin::getPercentValueText,
    SimpleOption.DoubleSliderCallbacks.INSTANCE,
    (double) MineCellsClient.CLIENT_CONFIG.screenShake.global,
    (it) -> {
      MineCellsClient.CLIENT_CONFIG.screenShake.global = it.floatValue();
      MineCellsClient.CLIENT_CONFIG.save();
      MineCellsClient.setupScreenShakeModifiers(it.floatValue());
    }
  );

  @Unique
  private static Text getPercentValueText(Text prefix, double value) {
    return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
  }

  @Unique
  @Override
  public SimpleOption<Double> minecells$getMinecellsScreenshake() {
    return minecellsScreenshake;
  }

  @Unique
  @Override
  public float minecells$getMinecellsScreenshakeValue() {
    return minecellsScreenshake.getValue().floatValue() * 5.0f;
  }
}
