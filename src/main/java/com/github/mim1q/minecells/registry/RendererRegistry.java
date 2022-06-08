package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.*;
import com.github.mim1q.minecells.client.render.model.*;
import com.github.mim1q.minecells.client.render.model.nonliving.ElevatorEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.BigGrenadeEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.DisgustingWormEggEntityModel;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.client.render.nonliving.ElevatorEntityRenderer;
import com.github.mim1q.minecells.client.render.nonliving.projectile.BigGrenadeEntityRenderer;
import com.github.mim1q.minecells.client.render.nonliving.projectile.DisgustingWormEggEntityRenderer;
import com.github.mim1q.minecells.client.render.nonliving.projectile.GrenadeEntityRenderer;
import com.github.mim1q.minecells.client.render.nonliving.projectile.MagicOrbEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class RendererRegistry {
    public static final EntityModelLayer LEAPING_ZOMBIE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "leaping_zombie"), "main");
    public static final EntityModelLayer SHOCKER_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "shocker"), "main");
    public static final EntityModelLayer GRENADIER_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "grenadier"), "main");
    public static final EntityModelLayer DISGUSTING_WORM_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "disgusting_worm"), "main");
    public static final EntityModelLayer INQUISITOR_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "inquisitor"), "main");
    public static final EntityModelLayer KAMIKAZE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "kamikaze"), "main");
    public static final EntityModelLayer PROTECTOR_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "protector"), "main");
    public static final EntityModelLayer UNDEAD_ARCHER_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "undead_archer"), "main");

    public static final EntityModelLayer GRENADE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "grenade_render"), "main");
    public static final EntityModelLayer BIG_GRENADE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "big_grenade_render"), "main");
    public static final EntityModelLayer DISGUSTING_WORM_EGG_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "disgusting_worm_egg_render"), "main");

    public static final EntityModelLayer ELEVATOR_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "elevator_render"), "main");

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(LEAPING_ZOMBIE_LAYER, LeapingZombieEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SHOCKER_LAYER, ShockerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(GRENADIER_LAYER, GrenadierEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DISGUSTING_WORM_LAYER, DisgustingWormEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(INQUISITOR_LAYER, InquisitorEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(KAMIKAZE_LAYER, KamikazeEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(PROTECTOR_LAYER, ProtectorEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(UNDEAD_ARCHER_LAYER, UndeadArcherEntityModel::getTexturedModelData);

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
        EntityRendererRegistry.register(EntityRegistry.UNDEAD_ARCHER, UndeadArcherEntityRenderer::new);

        EntityRendererRegistry.register(EntityRegistry.GRENADE, GrenadeEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.BIG_GRENADE, BigGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.DISGUSTING_WORM_EGG, DisgustingWormEggEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.MAGIC_ORB, MagicOrbEntityRenderer::new);

        EntityRendererRegistry.register(EntityRegistry.ELEVATOR, ElevatorEntityRenderer::new);
    }
}
