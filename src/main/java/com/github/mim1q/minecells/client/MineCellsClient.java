package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.client.renderer.JumpingZombieEntityRenderer;
import com.github.mim1q.minecells.registry.EntityRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class MineCellsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register renderers
        EntityRendererRegistry.INSTANCE.register(EntityRegistry.JUMPING_ZOMBIE, JumpingZombieEntityRenderer::new);
    }
}
