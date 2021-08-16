package com.github.mim1q.minecells;

import com.github.mim1q.minecells.registry.EntityRegistry;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineCells implements ModInitializer {
    public static final String MOD_ID = "minecells";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        // Construct registries
        new EntityRegistry();
    }
}
