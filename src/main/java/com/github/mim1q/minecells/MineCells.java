package com.github.mim1q.minecells;

import com.github.mim1q.minecells.config.CommonConfig;
import com.github.mim1q.minecells.registry.*;
import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineCells implements ModInitializer {

  public static final CommonConfig COMMON_CONFIG = OmegaConfig.register(CommonConfig.class);

  public static final String MOD_ID = "minecells";
  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onInitialize() {
    COMMON_CONFIG.save();
    EntityRegistry.register();
    SoundRegistry.register();
    BlockRegistry.register();
    BlockEntityRegistry.register();
    FluidRegistry.register();
    ItemRegistry.register();
    ItemGroupRegistry.register();
    StatusEffectRegistry.register();
    ParticleRegistry.register();
    BiomeRegistry.register();
    MineCellsPlacerTypes.register();
    PointOfInterestRegistry.register();
  }

  public static Identifier createId(String path) {
    return new Identifier(MOD_ID, path);
  }
}
