package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.particle.*;
import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.particle.colored.ColoredParticleType;
import com.github.mim1q.minecells.particle.electric.ElectricParticle;
import com.github.mim1q.minecells.particle.electric.ElectricParticleType;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MineCellsParticles {

  public static final DefaultParticleType AURA = FabricParticleTypes.simple();
  public static final DefaultParticleType EXPLOSION = FabricParticleTypes.simple();
  public static final DefaultParticleType PROTECTOR = FabricParticleTypes.simple();
  public static final DefaultParticleType CHARGE = FabricParticleTypes.simple();
  public static final DefaultParticleType FLY = FabricParticleTypes.simple();
  public static final ColoredParticleType SPECKLE = ColoredParticleType.create();
  public static final ColoredParticleType FALLING_LEAF = ColoredParticleType.create();
  public static final ElectricParticleType ELECTRICITY = ElectricParticleType.create();

  public static void init() {
    Registry.register(Registries.PARTICLE_TYPE, MineCells.createId("aura"), AURA);
    Registry.register(Registries.PARTICLE_TYPE, MineCells.createId("explosion"), EXPLOSION);
    Registry.register(Registries.PARTICLE_TYPE, MineCells.createId("protector"), PROTECTOR);
    Registry.register(Registries.PARTICLE_TYPE, MineCells.createId("charge"), CHARGE);
    Registry.register(Registries.PARTICLE_TYPE, MineCells.createId("fly"), FLY);
    Registry.register(Registries.PARTICLE_TYPE, MineCells.createId("speckle"), SPECKLE);
    Registry.register(Registries.PARTICLE_TYPE, MineCells.createId("falling_leaf"), FALLING_LEAF);
    Registry.register(Registries.PARTICLE_TYPE, MineCells.createId("electricity"), ELECTRICITY);
  }

  public static void initClient() {
    ParticleFactoryRegistry.getInstance().register(AURA, FlameParticle.Factory::new);
    ParticleFactoryRegistry.getInstance().register(EXPLOSION, ExplosionParticle.Factory::new);
    ParticleFactoryRegistry.getInstance().register(PROTECTOR, ProtectorParticle.Factory::new);
    ParticleFactoryRegistry.getInstance().register(CHARGE, ChargeParticle.Factory::new);
    ParticleFactoryRegistry.getInstance().register(FLY, FlyParticle.Factory::new);
    ParticleFactoryRegistry.getInstance().register(SPECKLE, ColoredParticle.createFactory(SpeckleParticle::new));
    ParticleFactoryRegistry.getInstance().register(FALLING_LEAF, ColoredParticle.createFactory(FallingLeafParticle::new));
    ParticleFactoryRegistry.getInstance().register(ELECTRICITY, ElectricParticle.Factory::new);
  }
}
