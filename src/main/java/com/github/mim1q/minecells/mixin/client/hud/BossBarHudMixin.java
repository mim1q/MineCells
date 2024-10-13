package com.github.mim1q.minecells.mixin.client.hud;

import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.gui.CustomBossBarRenderer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Priority is 1001 to be applied before any boss bar changing mods
@Mixin(value = BossBarHud.class, priority = 1001)
public class BossBarHudMixin {
  @Inject(
    method = "renderBossBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/boss/BossBar;)V",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$renderCustomBossBar(DrawContext context, int x, int y, BossBar bossBar, CallbackInfo ci) {
    if (!MineCellsClient.CLIENT_CONFIG.customBossBars()) return;
    if (CustomBossBarRenderer.renderCustomBossBar(context, x - 12, y - 8, bossBar)) ci.cancel();
  }

  @ModifyExpressionValue(
    method = "render",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/gui/hud/ClientBossBar;getName()Lnet/minecraft/text/Text;"
    )
  )
  private Text minecells$modifyGetBossBarName(Text original) {
    if (!MineCellsClient.CLIENT_CONFIG.customBossBars()) return original;
    return CustomBossBarRenderer.getCustomBossBarName(original);
  }
}
