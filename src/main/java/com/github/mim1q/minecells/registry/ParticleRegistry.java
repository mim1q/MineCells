package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleRegistry {

    public static final DefaultParticleType AURA = FabricParticleTypes.simple();
    public static final DefaultParticleType MAGIC_ORB = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MineCells.MOD_ID, "aura"), AURA);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MineCells.MOD_ID, "magic_orb"), MAGIC_ORB);
    }

    public static void registerClient() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
                registry.register(new Identifier(MineCells.MOD_ID, "particle/aura"));
                registry.register(new Identifier(MineCells.MOD_ID, "particle/magic_orb"));
                });

        ParticleFactoryRegistry.getInstance().register(AURA, FlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(MAGIC_ORB, FlameParticle.Factory::new);
    }
}
