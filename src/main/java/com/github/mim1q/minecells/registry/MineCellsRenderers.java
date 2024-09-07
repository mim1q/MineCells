package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.client.render.*;
import com.github.mim1q.minecells.client.render.blockentity.*;
import com.github.mim1q.minecells.client.render.blockentity.BarrierControllerRenderer.BarrierControllerModel;
import com.github.mim1q.minecells.client.render.blockentity.FlagBlockEntityRenderer.BiomeBannerBlockEntityModel;
import com.github.mim1q.minecells.client.render.blockentity.portal.DoorwayPortalBlockEntityRenderer;
import com.github.mim1q.minecells.client.render.blockentity.portal.RiftBlockEntityRenderer;
import com.github.mim1q.minecells.client.render.blockentity.portal.TeleporterBlockEntityRenderer;
import com.github.mim1q.minecells.client.render.blockentity.portal.TeleporterBlockEntityRenderer.TeleporterModel;
import com.github.mim1q.minecells.client.render.blockentity.statue.DecorativeStatueBlockEntityRenderer;
import com.github.mim1q.minecells.client.render.blockentity.statue.KingStatueModel;
import com.github.mim1q.minecells.client.render.conjunctivius.ConjunctiviusEntityRenderer;
import com.github.mim1q.minecells.client.render.conjunctivius.ConjunctiviusEyeRenderer;
import com.github.mim1q.minecells.client.render.conjunctivius.ConjunctiviusSpikeRenderer;
import com.github.mim1q.minecells.client.render.conjunctivius.ConjunctiviusTentacleRenderer;
import com.github.mim1q.minecells.client.render.item.BiomeBannerItemRenderer;
import com.github.mim1q.minecells.client.render.model.*;
import com.github.mim1q.minecells.client.render.model.conjunctivius.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.ElevatorEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.ObeliskEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.TentacleWeaponEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.BigGrenadeEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.ConjunctiviusProjectileEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.DisgustingWormEggEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.client.render.nonliving.*;
import com.github.mim1q.minecells.client.render.nonliving.projectile.*;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.item.DimensionalRuneItem;
import com.github.mim1q.minecells.item.weapon.bow.CustomBowItem;
import com.github.mim1q.minecells.item.weapon.bow.CustomCrossbowItem;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreen;
import com.github.mim1q.minecells.world.FoggyDimensionEffects;
import com.github.mim1q.minecells.world.PromenadeDimensionEffects;
import dev.mim1q.gimm1q.client.render.overlay.ModelOverlayFeatureRenderer;
import dev.mim1q.gimm1q.client.render.overlay.ModelOverlayVertexConsumer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.TexturedModelDataProvider;
import net.fabricmc.fabric.mixin.client.rendering.DimensionEffectsAccessor;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class MineCellsRenderers {
  private static boolean dynamicItemRenderersRegistered = false;

  private static <T extends Entity> EntityRenderer<T> invisibleRenderer(Context ctx) {
    return new EntityRenderer<>(ctx) {
      @Override
      public Identifier getTexture(Entity entity) {
        return null;
      }
    };
  }

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
  public static final EntityModelLayer FLY_LAYER = registerLayer("fly", FlyEntityModel::getTexturedModelData);
  public static final EntityModelLayer SWEEPER_LAYER = registerLayer("sweeper", SweeperEntityModel::getTexturedModelData);

  public static final EntityModelLayer CONJUNCTIVIUS_MAIN_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "main");
  public static final EntityModelLayer CONJUNCTIVIUS_EYE_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "eye");
  public static final EntityModelLayer CONJUNCTIVIUS_TENTACLE_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "tentacle");
  public static final EntityModelLayer CONJUNCTIVIUS_SPIKE_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius"), "spike");

  public static final EntityModelLayer CONCIERGE_LAYER = registerLayer("concierge", ConciergeEntityModel::getTexturedModelData);

  public static final EntityModelLayer GRENADE_LAYER = new EntityModelLayer(MineCells.createId("grenade"), "main");
  public static final EntityModelLayer BIG_GRENADE_LAYER = new EntityModelLayer(MineCells.createId("big_grenade"), "main");
  public static final EntityModelLayer DISGUSTING_WORM_EGG_LAYER = new EntityModelLayer(MineCells.createId("disgusting_worm_egg"), "main");
  public static final EntityModelLayer CONJUNCTIVIUS_PROJECTILE_LAYER = new EntityModelLayer(MineCells.createId("conjunctivius_projectile"), "main");

  public static final EntityModelLayer ELEVATOR_LAYER = new EntityModelLayer(MineCells.createId("elevator"), "main");
  public static final EntityModelLayer TENTACLE_WEAPON_LAYER = new EntityModelLayer(MineCells.createId("tentacle_weapon"), "main");
  public static final EntityModelLayer OBELISK_LAYER = new EntityModelLayer(MineCells.createId("obelisk"), "main");

  public static final EntityModelLayer BIG_DOOR_BARRIER_LAYER = registerLayer("barrier_controller", "big_door", BarrierControllerModel::getBigDoorTexturedModelData);

  public static final EntityModelLayer FLAG_LAYER = registerLayer("flag", () -> BiomeBannerBlockEntityModel.getTexturedModelData(false));
  public static final EntityModelLayer FLAG_LARGE_LAYER = registerLayer("flag", "large", () -> BiomeBannerBlockEntityModel.getTexturedModelData(true));
  public static final EntityModelLayer ARROW_SIGN_LAYER = registerLayer("arrow_sign", ArrowSignBlockEntityRenderer::getTexturedModelData);

  public static final EntityModelLayer KING_STATUE_LAYER = new EntityModelLayer(MineCells.createId("king_statue"), "main");
  public static final EntityModelLayer TELEPORTER_LAYER = registerLayer("teleporter", TeleporterModel::getTexturedModelData);

  private static EntityModelLayer registerLayer(String path, String name, TexturedModelDataProvider provider) {
    var layer = new EntityModelLayer(MineCells.createId(path), name);
    EntityModelLayerRegistry.registerModelLayer(layer, provider);
    return layer;
  }

  private static EntityModelLayer registerLayer(String path, TexturedModelDataProvider provider) {
    return registerLayer(path, "main", provider);
  }

  @SuppressWarnings("UnstableApiUsage")
  public static void init() {
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

    EntityModelLayerRegistry.registerModelLayer(GRENADE_LAYER, GrenadeEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(BIG_GRENADE_LAYER, BigGrenadeEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(DISGUSTING_WORM_EGG_LAYER, DisgustingWormEggEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(CONJUNCTIVIUS_PROJECTILE_LAYER, ConjunctiviusProjectileEntityModel::getTexturedModelData);

    EntityModelLayerRegistry.registerModelLayer(ELEVATOR_LAYER, ElevatorEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(TENTACLE_WEAPON_LAYER, TentacleWeaponEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(OBELISK_LAYER, ObeliskEntityModel::getTexturedModelData);

    EntityRendererRegistry.register(MineCellsEntities.LEAPING_ZOMBIE, LeapingZombieEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.SHOCKER, ShockerEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.GRENADIER, GrenadierEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.DISGUSTING_WORM, DisgustingWormEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.INQUISITOR, InquisitorEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.KAMIKAZE, KamikazeEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.PROTECTOR, ProtectorEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.UNDEAD_ARCHER, UndeadArcherEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.SHIELDBEARER, ShieldbearerEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.MUTATED_BAT, MutatedBatEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.SEWERS_TENTACLE, SewersTentacleEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.RANCID_RAT, RancidRatEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.RUNNER, RunnerEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.SCORPION, ScorpionEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.BUZZCUTTER, ctx -> new FlyEntityRenderer<>(ctx, "buzzcutter"));
    EntityRendererRegistry.register(MineCellsEntities.SWEEPER, SweeperEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.CONJUNCTIVIUS, ConjunctiviusEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.CONCIERGE, ConciergeEntityRenderer::new);

    EntityRendererRegistry.register(MineCellsEntities.GRENADE, GrenadeEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.BIG_GRENADE, BigGrenadeEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.DISGUSTING_WORM_EGG, DisgustingWormEggEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.MAGIC_ORB, MagicOrbEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.SCORPION_SPIT, ScorpionSpitEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.CONJUNCTIVIUS_PROJECTILE, ConjunctiviusProjectileEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.CUSTOM_ARROW, CustomArrowEntityRenderer::new);

    EntityRendererRegistry.register(MineCellsEntities.ELEVATOR, ElevatorEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.CELL, CellEntityRenderer::new);
    EntityRendererRegistry.register(MineCellsEntities.TENTACLE_WEAPON, TentacleWeaponEntityRenderer::new);

    EntityRendererRegistry.register(MineCellsEntities.CONJUNCTIVIUS_OBELISK, ctx -> new ObeliskEntityRenderer(ctx, "conjunctivius"));
    EntityRendererRegistry.register(MineCellsEntities.CONCIERGE_OBELISK, ctx -> new ObeliskEntityRenderer(ctx, "concierge"));
    EntityRendererRegistry.register(MineCellsEntities.ELITE_OBELISK, ctx -> new ObeliskEntityRenderer(ctx, "elite"));

    EntityRendererRegistry.register(MineCellsEntities.SHOCKWAVE_PLACER, MineCellsRenderers::invisibleRenderer);
    EntityRendererRegistry.register(MineCellsEntities.SPAWNER_RUNE, SpawnerRuneRenderer.Entity::new);

    DimensionEffectsAccessor.getIdentifierMap().put(MineCells.createId("foggy"), new FoggyDimensionEffects());
    DimensionEffectsAccessor.getIdentifierMap().put(MineCells.createId("promenade"), new PromenadeDimensionEffects());

    HandledScreens.register(MineCellsScreenHandlerTypes.CELL_FORGE_SCREEN_HANDLER, CellCrafterScreen::new);
  }

  public static void initBlocks() {
    FluidRenderHandlerRegistry.INSTANCE.register(MineCellsFluids.STILL_SEWAGE, MineCellsFluids.FLOWING_SEWAGE, new SimpleFluidRenderHandler(
      new Identifier("block/water_still"),
      new Identifier("block/water_flow"),
      0xA2E751
    ));
    FluidRenderHandlerRegistry.INSTANCE.register(MineCellsFluids.STILL_ANCIENT_SEWAGE, MineCellsFluids.FLOWING_ANCIENT_SEWAGE, new SimpleFluidRenderHandler(
      new Identifier("block/water_still"),
      new Identifier("block/water_flow"),
      0xE0C93B
    ));

    BlockRenderLayerMap.INSTANCE.putBlocks(
      RenderLayer.getCutout(),
      MineCellsBlocks.BIG_CHAIN,
      MineCellsBlocks.CHAIN_PILE,
      MineCellsBlocks.CAGE,
      MineCellsBlocks.BROKEN_CAGE,
      MineCellsBlocks.SPIKES,
      MineCellsBlocks.HANGED_SKELETON,
      MineCellsBlocks.SKELETON,
      MineCellsBlocks.HANGED_ROTTING_CORPSE,
      MineCellsBlocks.HANGED_CORPSE,
      MineCellsBlocks.WILTED_LEAVES.leaves,
      MineCellsBlocks.WILTED_LEAVES.hangingLeaves,
      MineCellsBlocks.WILTED_LEAVES.wallLeaves,
      MineCellsBlocks.ORANGE_WILTED_LEAVES.leaves,
      MineCellsBlocks.ORANGE_WILTED_LEAVES.hangingLeaves,
      MineCellsBlocks.ORANGE_WILTED_LEAVES.wallLeaves,
      MineCellsBlocks.RED_WILTED_LEAVES.leaves,
      MineCellsBlocks.RED_WILTED_LEAVES.hangingLeaves,
      MineCellsBlocks.RED_WILTED_LEAVES.wallLeaves,
      MineCellsBlocks.RUNIC_VINE,
      MineCellsBlocks.RUNIC_VINE_PLANT,
      MineCellsBlocks.UNBREAKABLE_CHAIN,
      MineCellsBlocks.CELL_CRAFTER,
      MineCellsBlocks.UNBREAKABLE_CELL_CRAFTER,
      MineCellsBlocks.ALCHEMY_EQUIPMENT_0,
      MineCellsBlocks.ALCHEMY_EQUIPMENT_2,
      MineCellsBlocks.PUTRID_WOOD.door,
      MineCellsBlocks.PRISON_TORCH,
      MineCellsBlocks.PROMENADE_TORCH,
      MineCellsBlocks.RAMPARTS_TORCH,
      MineCellsBlocks.WILTED_GRASS_BLOCK,
      MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK,
      MineCellsBlocks.RED_PUTRID_SAPLING,
      MineCellsBlocks.ORANGE_PUTRID_SAPLING,
      MineCellsBlocks.PUTRID_SAPLING,
      MineCellsBlocks.SHOCKWAVE_FLAME,
      MineCellsBlocks.SHOCKWAVE_FLAME_PLAYER
    );
    BlockRenderLayerMap.INSTANCE.putBlocks(
      RenderLayer.getTranslucent(),
      MineCellsBlocks.ALCHEMY_EQUIPMENT_1
    );

    BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), MineCellsFluids.STILL_SEWAGE, MineCellsFluids.FLOWING_SEWAGE);

    EntityModelLayerRegistry.registerModelLayer(KING_STATUE_LAYER, KingStatueModel::getTexturedModelData);

    BlockEntityRendererFactories.register(MineCellsBlockEntities.FLAG_BLOCK_ENTITY, FlagBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(MineCellsBlockEntities.DECORATIVE_STATUE_BLOCK_ENTITY, DecorativeStatueBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(MineCellsBlockEntities.RETURN_STONE, ReturnStoneBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(MineCellsBlockEntities.RUNIC_VINE_PLANT, RunicVinePlantBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(MineCellsBlockEntities.ARROW_SIGN, ArrowSignBlockEntityRenderer::new);

    BlockEntityRendererFactories.register(MineCellsBlockEntities.BARRIER_CONTROLLER, BarrierControllerRenderer::new);

    BlockEntityRendererFactories.register(MineCellsBlockEntities.TELEPORTER, TeleporterBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(MineCellsBlockEntities.DOORWAY, DoorwayPortalBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(MineCellsBlockEntities.RIFT, RiftBlockEntityRenderer::new);

    BlockEntityRendererFactories.register(MineCellsBlockEntities.CELL_CRAFTER, ctx -> new CellCrafterBlockEntityRenderer());

    BlockEntityRendererFactories.register(MineCellsBlockEntities.SPAWNER_RUNE, SpawnerRuneRenderer.BlockEntity::new);

    ModelPredicateProviderRegistry.register(
      MineCellsItems.HATTORIS_KATANA,
      new Identifier("blocking"),
      (stack, world, entity, i) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
    );

    ModelPredicateProviderRegistry.register(
      MineCellsItems.CELL_HOLDER,
      MineCells.createId("cells"),
      (stack, world, entity, seed) -> {
        var nbt = stack.getNbt();
        if (nbt == null) return 0f;

        var cells = nbt.getInt("Cells");
        if (cells > 128) return 1.0f;
        if (cells > 64) return 0.75f;
        if (cells > 0) return 0.5f;
        return 0f;
      }
    );

    MineCellsItems.BOWS.forEach(MineCellsRenderers::registerBowPredicate);
    MineCellsItems.CROSSBOWS.forEach(MineCellsRenderers::registerCrossbowPredicate);
    MineCellsItems.SHIELDS.forEach(MineCellsRenderers::registerShieldPredicate);

    ColorProviderRegistry.BLOCK.register(
      (state, world, pos, tintIndex) -> world == null ? 0x80CC80 : BiomeColors.getFoliageColor(world, pos),
      MineCellsBlocks.WILTED_LEAVES.leaves, MineCellsBlocks.WILTED_LEAVES.hangingLeaves, MineCellsBlocks.WILTED_LEAVES.wallLeaves
    );

    ColorProviderRegistry.BLOCK.register(
      (state, world, pos, tintIndex) -> world == null ? 0x80CC80 : BiomeColors.getGrassColor(world, pos),
      MineCellsBlocks.WILTED_GRASS_BLOCK, MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK
    );

    ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x80CC80,
      MineCellsBlocks.WILTED_LEAVES.leaves, MineCellsBlocks.WILTED_LEAVES.hangingLeaves, MineCellsBlocks.WILTED_LEAVES.wallLeaves,
      MineCellsBlocks.WILTED_GRASS_BLOCK, MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK
    );

    ColorProviderRegistry.ITEM.register(
      (stack, tintIndex) -> tintIndex == 1
        ? ((DimensionalRuneItem) stack.getItem()).portalBlock.type.color
        : 0xFFFFFF,
      MineCellsItems.DIMENSIONAL_RUNES.toArray(new DimensionalRuneItem[0])
    );

    for (var entry : MineCellsItems.DOORWAY_COLORS.entrySet()) {
      ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? entry.getValue() : 0xFFFFFF, entry.getKey());
    }

    // I've accepted this code just like it is
    // What really matters is on the inside (and that it somehow works)
    LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
      ((entityType, entityRenderer, registrationHelper, context) -> {
        if (!dynamicItemRenderersRegistered) {
          for (var flag : MineCellsBlocks.FLAG_BLOCKS) {
            BuiltinItemRendererRegistry.INSTANCE.register(
              flag,
              new BiomeBannerItemRenderer(context.getModelLoader(), flag)
            );
            dynamicItemRenderersRegistered = true;
          }
        }
      })
    );


    // Actual feature renderers:

    final var iceTexture = new Identifier("textures/block/ice.png");

    LivingEntityFeatureRendererRegistrationCallback.EVENT.register(

      ((entityType, entityRenderer, registrationHelper, context) -> {

        //noinspection unchecked
        registrationHelper.register(ModelOverlayFeatureRenderer.of(
          (entity) -> ((LivingEntityAccessor) entity).getMineCellsFlag(MineCellsEffectFlags.FROZEN),
          (entity, vertexConsumers) -> ModelOverlayVertexConsumer
            .of(vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(iceTexture)))
            .offset(2.0322f)
            .skipPlanes(),
          true
        ).apply((FeatureRendererContext<LivingEntity, EntityModel<LivingEntity>>) entityRenderer));
      })
    );
  }

  private static void registerBowPredicate(CustomBowItem item) {
    ModelPredicateProviderRegistry.register(item, MineCells.createId("pulling"), (stack, world, entity, seed) ->
      entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
    );
    ModelPredicateProviderRegistry.register(item, MineCells.createId("pull"), (stack, world, entity, seed) -> {
      if (entity == null) {
        return 0.0F;
      } else {
        var stackItem = (CustomBowItem) stack.getItem();
        return entity.getActiveItem() != stack
          ? 0.0F
          : (float) entity.getItemUseTime() / stackItem.getDrawTime(stack);
      }
    });
  }

  private static void registerCrossbowPredicate(CustomCrossbowItem item) {
    ModelPredicateProviderRegistry.register(item, MineCells.createId("pull"), (stack, world, entity, seed) -> {
      if (entity == null) {
        return 0.0F;
      } else {
        var stackItem = (CustomCrossbowItem) stack.getItem();
        return CrossbowItem.isCharged(stack)
          ? 0.0F
          : (float) entity.getItemUseTime() / stackItem.getDrawTime(stack);
      }
    });

    ModelPredicateProviderRegistry.register(
      item,
      MineCells.createId("pulling"),
      (stack, world, entity, seed) -> entity != null
        && entity.isUsingItem()
        && entity.getActiveItem() == stack
        && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);

    ModelPredicateProviderRegistry.register(
      item,
      MineCells.createId("charged"),
      (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) ? 1.0F : 0.0F
    );
  }

  private static void registerShieldPredicate(Item item) {
    ModelPredicateProviderRegistry.register(item, MineCells.createId("blocking"), (stack, world, entity, seed) ->
      entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
    );
  }
}