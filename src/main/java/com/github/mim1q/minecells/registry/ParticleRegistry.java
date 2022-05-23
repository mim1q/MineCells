package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.particle.ExplosionParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleRegistry {

    public static final DefaultParticleType AURA = FabricParticleTypes.simple();
    public static final DefaultParticleType EXPLOSION = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MineCells.MOD_ID, "aura"), AURA);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MineCells.MOD_ID, "explosion"), EXPLOSION);
    }

    public static void registerClient() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(new Identifier(MineCells.MOD_ID, "particle/aura"));
            registry.register(new Identifier(MineCells.MOD_ID, "particle/explosion"));
            });

        ParticleFactoryRegistry.getInstance().register(AURA, FlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EXPLOSION, ExplosionParticle.Factory::new);
    }
}
