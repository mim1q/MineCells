package com.github.mim1q.minecells;

import com.github.mim1q.minecells.config.ServerConfig;
import com.github.mim1q.minecells.registry.*;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineCells implements ModInitializer {

    @Environment(EnvType.SERVER)
    public static final ServerConfig SERVER_CONFIG = OmegaConfig.register(ServerConfig.class);

    public static final String MOD_ID = "minecells";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            SERVER_CONFIG.correctValues();
            SERVER_CONFIG.save();
        }
        System.out.println(SERVER_CONFIG.toString());

        EntityRegistry.register();
        SoundRegistry.register();
        BlockRegistry.register();
        ItemRegistry.register();
        ItemGroupRegistry.register();
        StatusEffectRegistry.register();
        ParticleRegistry.register();
    }
}
