package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.*;
import com.github.mim1q.minecells.client.model.nonliving.ElevatorEntityModel;
import com.github.mim1q.minecells.client.model.projectile.BigGrenadeEntityModel;
import com.github.mim1q.minecells.client.model.projectile.DisgustingWormEggEntityModel;
import com.github.mim1q.minecells.client.model.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.client.renderer.*;
import com.github.mim1q.minecells.client.renderer.nonliving.ElevatorEntityRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.BigGrenadeEntityRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.DisgustingWormEggEntityRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.GrenadeEntityRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.MagicOrbEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class RendererRegistry {
    public static final EntityModelLayer LEAPING_ZOMBIE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "leaping_zombie_layer"), "leaping_zombie_layer");
    public static final EntityModelLayer SHOCKER_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "shocker_layer"), "shocker_layer");
    public static final EntityModelLayer GRENADIER_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "grenadier_layer"), "grenadier_layer");
    public static final EntityModelLayer DISGUSTING_WORM_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "disgusting_worm"), "disgusting_worm");
    public static final EntityModelLayer INQUISITOR_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "inquisitor_layer"), "inquisitor_layer");
    public static final EntityModelLayer KAMIKAZE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "kamikaze_layer"), "kamikaze_layer");
    public static final EntityModelLayer PROTECTOR_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "protector"), "protector");

    public static final EntityModelLayer GRENADE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "grenade_render_layer"), "grenade_render_layer");
    public static final EntityModelLayer BIG_GRENADE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "big_grenade_render_layer"), "big_grenade_render_layer");
    public static final EntityModelLayer DISGUSTING_WORM_EGG_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "disgusting_worm_egg_render_layer"), "disgusting_worm_egg_render_layer");

    public static final EntityModelLayer ELEVATOR_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "elevator_render_layer"), "elevator_render_layer");

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(LEAPING_ZOMBIE_LAYER, LeapingZombieEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SHOCKER_LAYER, ShockerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(GRENADIER_LAYER, GrenadierEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DISGUSTING_WORM_LAYER, DisgustingWormEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(INQUISITOR_LAYER, InquisitorEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(KAMIKAZE_LAYER, KamikazeEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(PROTECTOR_LAYER, ProtectorEntityModel::getTexturedModelData);

        EntityModelLayerRegistry.registerModelLayer(GRENADE_LAYER, GrenadeEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BIG_GRENADE_LAYER, BigGrenadeEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DISGUSTING_WORM_EGG_LAYER, DisgustingWormEggEntityModel::getTexturedModelData);

        EntityModelLayerRegistry.registerModelLayer(ELEVATOR_LAYER, ElevatorEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(EntityRegistry.LEAPING_ZOMBIE, LeapingZombieEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.SHOCKER, ShockerEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.GRENADIER, GrenadierEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.DISGUSTING_WORM, DisgustingWormEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.INQUISITOR, InquisitorEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.KAMIKAZE, KamikazeEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.PROTECTOR, ProtectorEntityRenderer::new);

        EntityRendererRegistry.register(EntityRegistry.GRENADE, GrenadeEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.BIG_GRENADE, BigGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.DISGUSTING_WORM_EGG, DisgustingWormEggEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.MAGIC_ORB, MagicOrbEntityRenderer::new);

        EntityRendererRegistry.register(EntityRegistry.ELEVATOR, ElevatorEntityRenderer::new);
    }
}
