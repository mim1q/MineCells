package com.github.mim1q.minecells;

import com.github.mim1q.minecells.config.ClientConfig;
import com.github.mim1q.minecells.network.ClientPacketHandler;
import com.github.mim1q.minecells.registry.MineCellsItemGroups;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import dev.mim1q.gimm1q.screenshake.ScreenShakeModifiers;
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
    MineCellsItemGroups.init();
    ClientPacketHandler.init();
    MineCellsParticles.initClient();

    setupScreenShakeModifiers();
  }

  private void setupScreenShakeModifiers() {
    // Conjunctivius
    ScreenShakeModifiers.setModifier("conjunctiviusSmash", CLIENT_CONFIG.screenShake.conjunctiviusSmash);
    ScreenShakeModifiers.setModifier("conjunctiviusRoar", CLIENT_CONFIG.screenShake.conjunctiviusRoar);
    ScreenShakeModifiers.setModifier("conjunctiviusDeath", CLIENT_CONFIG.screenShake.conjunctiviusDeath);

    // Concierge
    ScreenShakeModifiers.setModifier("conciergeLeap", CLIENT_CONFIG.screenShake.conciergeLeap);
    ScreenShakeModifiers.setModifier("conciergeStep", CLIENT_CONFIG.screenShake.conciergeStep);
    ScreenShakeModifiers.setModifier("conciergeDeath", CLIENT_CONFIG.screenShake.conciergeDeath);
  }
}
