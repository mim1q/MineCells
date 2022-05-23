package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.ProtectorEntityModel;
import com.github.mim1q.minecells.client.renderer.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.entity.ProtectorEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ProtectorEntityRenderer extends MobEntityRenderer<ProtectorEntity, ProtectorEntityModel> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/protector/protector.png");
    private static final Identifier GLOW_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/protector/protector_glow.png");

    public ProtectorEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ProtectorEntityModel(ctx.getPart(RendererRegistry.PROTECTOR_LAYER)), 0.35F);
        this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
    }

    @Override
    public Identifier getTexture(ProtectorEntity entity) {
        return TEXTURE;
    }
}
