package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.MineCellsClient;
import com.github.mim1q.minecells.client.model.GrenadierEntityModel;
import com.github.mim1q.minecells.client.renderer.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.entity.GrenadierEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class GrenadierEntityRenderer extends MobEntityRenderer<GrenadierEntity, GrenadierEntityModel> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/grenadier/grenadier.png");
    private static final Identifier GLOW_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/grenadier/grenadier_glow.png");

    public GrenadierEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new GrenadierEntityModel(ctx.getPart(RendererRegistry.GRENADIER_LAYER)), 0.35F);
        if (MineCellsClient.CLIENT_CONFIG.rendering.grenadierGlow) {
            this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
        }
    }

    @Override
    public Identifier getTexture(GrenadierEntity entity) {
        return TEXTURE;
    }
}
