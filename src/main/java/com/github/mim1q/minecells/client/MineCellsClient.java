package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.network.ClientPacketHandler;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MineCellsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RendererRegistry.register();
        ClientPacketHandler.register();
        ParticleRegistry.registerClient();
    }
}
