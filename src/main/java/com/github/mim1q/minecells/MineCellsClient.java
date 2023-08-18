package com.github.mim1q.minecells;

import com.github.mim1q.minecells.config.ClientConfig;
import com.github.mim1q.minecells.network.ClientPacketHandler;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MineCellsClient implements ClientModInitializer {

  public static final ClientConfig CLIENT_CONFIG = OmegaConfig.register(ClientConfig.class);

  @Override
  public void onInitializeClient() {
    CLIENT_CONFIG.save();
    MineCellsRenderers.init();
    MineCellsRenderers.initBlocks();
    ClientPacketHandler.init();
    MineCellsParticles.initClient();
  }
}
