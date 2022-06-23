package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.ScorpionEntityModel;
import com.github.mim1q.minecells.entity.ScorpionEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ScorpionEntityRenderer extends MobEntityRenderer<ScorpionEntity, ScorpionEntityModel> {

    public static final Identifier TEXTURE = MineCells.createId("textures/entity/scorpion/scorpion.png");
    public static final Identifier TEXTURE_GLOW = MineCells.createId("textures/entity/scorpion/scorpion_glow.png");

    public ScorpionEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ScorpionEntityModel(context.getPart(RendererRegistry.SCORPION_LAYER)), 0.75F);
        if (MineCellsClient.CLIENT_CONFIG.rendering.scorpionGlow) {
            this.addFeature(new GlowFeatureRenderer<>(this, TEXTURE_GLOW));
        }
    }

    @Override
    public Identifier getTexture(ScorpionEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(ScorpionEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.shadowRadius = mobEntity.isSleeping() ? 0.0F : 0.75F;
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
