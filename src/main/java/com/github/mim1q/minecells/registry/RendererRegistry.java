package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.*;
import com.github.mim1q.minecells.client.render.conjunctivius.*;
import com.github.mim1q.minecells.client.render.model.*;
import com.github.mim1q.minecells.client.render.model.conjunctivius.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.ElevatorEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.BigGrenadeEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.ConjunctiviusProjectileEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.DisgustingWormEggEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.client.render.nonliving.CellEntityRenderer;
import com.github.mim1q.minecells.client.render.nonliving.ElevatorEntityRenderer;
import com.github.mim1q.minecells.client.render.nonliving.projectile.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class RendererRegistry {
  public static final EntityModelLayer LEAPING_ZOMBIE_LAYER = new EntityModelLayer(MineCells.createId("leaping_zombie"), "main");
  public static final EntityModelLayer SHOCKER_LAYER = new EntityModelLayer(MineCells.createId("shocker"), "main");
  public static final EntityModelLayer GRENADIER_LAYER = new EntityModelLayer(MineCells.createId("grenadier"), "main");
  public static final EntityModelLayer DISGUSTING_WORM_LAYER = new EntityModelLayer(MineCells.createId("disgusting_worm"), "main");
  public static final EntityModelLayer INQUISITOR_LAYER = new EntityModelLayer(MineCells.createId("inquisitor"), "main");
  public static final EntityModelLayer KAMIKAZE_LAYER = new EntityModelLayer(MineCells.createId("kamikaze"), "main");
  public static final EntityModelLayer PROTECTOR_LAYER = new EntityModelLayer(MineCells.createId("protector"), "main");
  public static final EntityModelLayer UNDEAD_ARCHER_LAYER = new EntityModelLayer(MineCells.createId("undead_archer"), "main");
  public static final EntityModelLayer SHIELDBEARER_LAYER = new EntityModelLayer(MineCells.createId("shieldbearer"), "main");
  public static final EntityModelLayer MUTATED_BAT_LAYER = new EntityModelLayer(MineCells.createId("mutated_bat"), "main");
  public static final EntityModelLayer SEWERS_TENTACLE_LAYER = new EntityModelLayer(MineCells.createId("sewers_tentacle"), "main");
  public static final EntityModelLayer RANCID_RAT_LAYER = new EntityModelLayer(MineCells.createId("rancid_rat"), "main");
  public static final EntityModelLayer RUNNER_LAYER = new EntityModelLayer(MineCells.createId("runner"), "main");
  public static final EntityModelLayer SCORPION_LAYER = new EntityModelLayer(MineCells.createId("scorpion"), "main");
  public static final EntityModelLayer CONJUNCTIVIUS_MAIN_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "main");
  public static final EntityModelLayer CONJUNCTIVIUS_EYE_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "eye");
  public static final EntityModelLayer CONJUNCTIVIUS_TENTACLE_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "tentacle");
  public static final EntityModelLayer CONJUNCTIVIUS_SPIKE_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "spike");
  public static final EntityModelLayer CONJUNCTIVIUS_CHAIN_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "chain");

  public static final EntityModelLayer GRENADE_LAYER = new EntityModelLayer(MineCells.createId("grenade"), "main");
  public static final EntityModelLayer BIG_GRENADE_LAYER = new EntityModelLayer(MineCells.createId("big_grenade"), "main");
  public static final EntityModelLayer DISGUSTING_WORM_EGG_LAYER = new EntityModelLayer(MineCells.createId("disgusting_worm_egg"), "main");
  public static final EntityModelLayer CONJUNCTIVIUS_PROJECTILE_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius_projectile"), "main");

  public static final EntityModelLayer ELEVATOR_LAYER = new EntityModelLayer(MineCells.createId("elevator_render"), "main");

  public static void register() {
    EntityModelLayerRegistry.registerModelLayer(LEAPING_ZOMBIE_LAYER, LeapingZombieEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(SHOCKER_LAYER, ShockerEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(GRENADIER_LAYER, GrenadierEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(DISGUSTING_WORM_LAYER, DisgustingWormEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(INQUISITOR_LAYER, InquisitorEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(KAMIKAZE_LAYER, KamikazeEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(PROTECTOR_LAYER, ProtectorEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(UNDEAD_ARCHER_LAYER, UndeadArcherEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(SHIELDBEARER_LAYER, ShieldbearerEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(MUTATED_BAT_LAYER, MutatedBatEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(SEWERS_TENTACLE_LAYER, SewersTentacleEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(RANCID_RAT_LAYER, RancidRatEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(RUNNER_LAYER, RunnerEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(SCORPION_LAYER, ScorpionEntityModel::getTexturedModelData);

    EntityModelLayerRegistry.registerModelLayer(CONJUNCTIVIUS_MAIN_LAYER, ConjunctiviusEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(CONJUNCTIVIUS_EYE_LAYER, ConjunctiviusEyeRenderer.ConjunctiviusEyeModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(CONJUNCTIVIUS_TENTACLE_LAYER, ConjunctiviusTentacleRenderer.ConjunctiviusTentacleModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(CONJUNCTIVIUS_SPIKE_LAYER, ConjunctiviusSpikeRenderer.ConjunctiviusSpikeModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(CONJUNCTIVIUS_CHAIN_LAYER, ConjunctiviusChainRenderer.ConjunctiviusChainModel::getTexturedModelData);

    EntityModelLayerRegistry.registerModelLayer(GRENADE_LAYER, GrenadeEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(BIG_GRENADE_LAYER, BigGrenadeEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(DISGUSTING_WORM_EGG_LAYER, DisgustingWormEggEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(CONJUNCTIVIUS_PROJECTILE_LAYER, ConjunctiviusProjectileEntityModel::getTexturedModelData);

    EntityModelLayerRegistry.registerModelLayer(ELEVATOR_LAYER, ElevatorEntityModel::getTexturedModelData);

    EntityRendererRegistry.register(EntityRegistry.LEAPING_ZOMBIE, LeapingZombieEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.SHOCKER, ShockerEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.GRENADIER, GrenadierEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.DISGUSTING_WORM, DisgustingWormEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.INQUISITOR, InquisitorEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.KAMIKAZE, KamikazeEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.PROTECTOR, ProtectorEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.UNDEAD_ARCHER, UndeadArcherEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.SHIELDBEARER, ShieldbearerEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.MUTATED_BAT, MutatedBatEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.SEWERS_TENTACLE, SewersTentacleEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.RANCID_RAT, RancidRatEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.RUNNER, RunnerEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.SCORPION, ScorpionEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.CONJUNCTIVIUS, ConjunctiviusEntityRenderer::new);

    EntityRendererRegistry.register(EntityRegistry.GRENADE, GrenadeEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.BIG_GRENADE, BigGrenadeEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.DISGUSTING_WORM_EGG, DisgustingWormEggEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.MAGIC_ORB, MagicOrbEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.SCORPION_SPIT, ScorpionSpitEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.CONJUNCTIVIUS_PROJECTILE, ConjunctiviusProjectileEntityRenderer::new);

    EntityRendererRegistry.register(EntityRegistry.ELEVATOR, ElevatorEntityRenderer::new);
    EntityRendererRegistry.register(EntityRegistry.CELL, CellEntityRenderer::new);
  }

  public static void registerBlocks() {
    FluidRenderHandlerRegistry.INSTANCE.register(FluidRegistry.STILL_SEWAGE, FluidRegistry.FLOWING_SEWAGE, new SimpleFluidRenderHandler(
      new Identifier("block/water_still"),
      new Identifier("block/water_flow"),
      0xA2E751
    ));
    FluidRenderHandlerRegistry.INSTANCE.register(FluidRegistry.STILL_ANCIENT_SEWAGE, FluidRegistry.FLOWING_ANCIENT_SEWAGE, new SimpleFluidRenderHandler(
      new Identifier("block/water_still"),
      new Identifier("block/water_flow"),
      0xE0C93B
    ));

    BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.BIG_CHAIN, RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidRegistry.STILL_SEWAGE, FluidRegistry.FLOWING_SEWAGE);
  }
}
