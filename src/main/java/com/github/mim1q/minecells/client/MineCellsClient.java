package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.client.renderer.GrenadierEntityRenderer;
import com.github.mim1q.minecells.client.renderer.JumpingZombieEntityRenderer;
import com.github.mim1q.minecells.client.renderer.ShockerEntityRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.GrenadeEntityRenderer;
import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class MineCellsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RendererRegistry.register();
        ParticleRegistry.registerClient();
    }
}
