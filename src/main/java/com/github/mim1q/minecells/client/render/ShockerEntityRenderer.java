package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.render.feature.ShockerGlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.ShockerEntityModel;
import com.github.mim1q.minecells.entity.ShockerEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ShockerEntityRenderer extends MobEntityRenderer<ShockerEntity, ShockerEntityModel> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/shocker/shocker.png");
    private static final Identifier GLOW_TEXTURE_NORMAL = new Identifier(MineCells.MOD_ID, "textures/entity/shocker/shocker_glow.png");
    private static final Identifier GLOW_TEXTURE_ANGRY = new Identifier(MineCells.MOD_ID, "textures/entity/shocker/shocker_glow_angry.png");

    public ShockerEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ShockerEntityModel(ctx.getPart(RendererRegistry.SHOCKER_LAYER)), 0.5F);
        if (MineCellsClient.CLIENT_CONFIG.rendering.shockerGlow) {
            this.addFeature(new ShockerGlowFeatureRenderer(this, GLOW_TEXTURE_NORMAL, GLOW_TEXTURE_ANGRY));
        }
    }

    @Override
    public Identifier getTexture(ShockerEntity entity) {
        return TEXTURE;
    }
}
