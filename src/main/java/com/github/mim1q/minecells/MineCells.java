package com.github.mim1q.minecells;

import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.ItemRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.registry.StatusEffectRegistry;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

public class MineCells implements ModInitializer {
    public static final String MOD_ID = "minecells";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        GeckoLib.initialize();
        // Construct registries
        EntityRegistry.register();
        SoundRegistry.register();
        ItemRegistry.register();
        StatusEffectRegistry.register();
    }
}
