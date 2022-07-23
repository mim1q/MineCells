package com.github.mim1q.minecells;

import com.github.mim1q.minecells.client.gui.CellAmountHud;
import com.github.mim1q.minecells.config.ClientConfig;
import com.github.mim1q.minecells.network.ClientPacketHandler;
import com.github.mim1q.minecells.registry.BlockRegistry;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.RendererRegistry;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class MineCellsClient implements ClientModInitializer {

  public static final ClientConfig CLIENT_CONFIG = OmegaConfig.register(ClientConfig.class);

  public static CellAmountHud cellAmountHud;

  @Override
  public void onInitializeClient() {
    CLIENT_CONFIG.save();
    RendererRegistry.register();
    ClientPacketHandler.register();
    ParticleRegistry.registerClient();
    BlockRegistry.registerClient();
    ClientLifecycleEvents.CLIENT_STARTED.register((client) -> cellAmountHud = new CellAmountHud(client));
    HudRenderCallback.EVENT.register((matrixStack, delta) -> cellAmountHud.render(matrixStack, delta));
  }
}
