package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.DisgustingWormEntityModel;
import com.github.mim1q.minecells.client.renderer.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.entity.DisgustingWormEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class DisgustingWormEntityRenderer extends MobEntityRenderer<DisgustingWormEntity, DisgustingWormEntityModel> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/disgusting_worm/disgusting_worm.png");
    private static final Identifier GLOW_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/disgusting_worm/disgusting_worm_glow.png");

    public DisgustingWormEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DisgustingWormEntityModel(ctx.getPart(RendererRegistry.DISGUSTING_WORM_LAYER)), 0.75F);
        this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
    }

    @Override
    public Identifier getTexture(DisgustingWormEntity entity) {
        return TEXTURE;
    }
}
