package com.github.mim1q.minecells.mixin.client.hud;

import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.gui.ConjunctiviusClientBossBar;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(targets = "net.minecraft.client.gui.hud.BossBarHud$1")
public class BossBarUpdaterMixin {
  @WrapOperation(
    method = "add",
    at = @At(
      value = "NEW",
      target = "Lnet/minecraft/client/gui/hud/ClientBossBar;"
    )
  )
  private ClientBossBar minecells$onAddBossBar(
    UUID uuid,
    Text name,
    float percent,
    BossBar.Color color,
    BossBar.Style style,
    boolean darkenSky,
    boolean dragonMusic,
    boolean thickenFog,
    Operation<ClientBossBar> original
  ) {
    if (MineCellsClient.CLIENT_CONFIG.customBossBars
      && name.getContent() instanceof TranslatableTextContent content
      && content.getKey().equals("entity.minecells.conjunctivius")
    ) {
      return new ConjunctiviusClientBossBar(
        uuid,
        name,
        percent,
        color,
        style,
        darkenSky,
        dragonMusic,
        thickenFog
      );
    }

    return original.call(
      uuid,
      name,
      percent,
      color,
      style,
      darkenSky,
      dragonMusic,
      thickenFog
    );
  }
}
