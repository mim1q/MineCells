package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.particle.ChargeParticle;
import com.github.mim1q.minecells.particle.ExplosionParticle;
import com.github.mim1q.minecells.particle.ProtectorParticle;
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
    public static final DefaultParticleType PROTECTOR = FabricParticleTypes.simple();
    public static final DefaultParticleType CHARGE = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MineCells.MOD_ID, "aura"), AURA);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MineCells.MOD_ID, "explosion"), EXPLOSION);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MineCells.MOD_ID, "protector"), PROTECTOR);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MineCells.MOD_ID, "charge"), CHARGE);
    }

    public static void registerClient() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(new Identifier(MineCells.MOD_ID, "particle/aura"));
            registry.register(new Identifier(MineCells.MOD_ID, "particle/explosion"));
            registry.register(new Identifier(MineCells.MOD_ID, "particle/protector"));
            registry.register(new Identifier(MineCells.MOD_ID, "particle/charge"));
            });

        ParticleFactoryRegistry.getInstance().register(AURA, FlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EXPLOSION, ExplosionParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(PROTECTOR, ProtectorParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CHARGE, ChargeParticle.Factory::new);
    }
}
