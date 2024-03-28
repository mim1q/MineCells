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
    // Weapons
    ScreenShakeModifiers.setModifier("weapon_flint", CLIENT_CONFIG.screenShake.weaponFlint);

    // Conjunctivius
    ScreenShakeModifiers.setModifier("conjunctivius_smash", CLIENT_CONFIG.screenShake.conjunctiviusSmash);
    ScreenShakeModifiers.setModifier("conjunctivius_roar", CLIENT_CONFIG.screenShake.conjunctiviusRoar);
    ScreenShakeModifiers.setModifier("conjunctivius_death", CLIENT_CONFIG.screenShake.conjunctiviusDeath);

    // Concierge
    ScreenShakeModifiers.setModifier("concierge_leap", CLIENT_CONFIG.screenShake.conciergeLeap);
    ScreenShakeModifiers.setModifier("concierge_step", CLIENT_CONFIG.screenShake.conciergeStep);
    ScreenShakeModifiers.setModifier("concierge_roar", CLIENT_CONFIG.screenShake.conciergeRoar);
    ScreenShakeModifiers.setModifier("concierge_death", CLIENT_CONFIG.screenShake.conciergeDeath);
  }
}
