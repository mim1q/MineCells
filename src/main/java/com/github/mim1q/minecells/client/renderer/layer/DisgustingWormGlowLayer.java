package com.github.mim1q.minecells.client.renderer.layer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.DisgustingWormEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DisgustingWormGlowLayer extends GeoLayerRenderer<DisgustingWormEntity> {

    public static final Identifier LAYER = new Identifier(MineCells.MOD_ID, "textures/entity/disgusting_worm/disgusting_worm_glow.png");
    public static final Identifier MODEL = new Identifier(MineCells.MOD_ID, "geo/entity/disgusting_worm.geo.json");

    public DisgustingWormGlowLayer(IGeoRenderer<DisgustingWormEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, DisgustingWormEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderLayer cameo =  RenderLayer.getEyes(LAYER);
        this.getRenderer().render(this.getEntityModel().getModel(MODEL), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
                bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
    }
}