package com.github.mim1q.minecells.client.renderer.layer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.ShockerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class ShockerGlowLayer extends GeoLayerRenderer<ShockerEntity> {

    public static final Identifier LAYER_NORMAL = new Identifier(MineCells.MOD_ID, "textures/entity/shocker/shocker_glow.png");
    public static final Identifier LAYER_ANGRY = new Identifier(MineCells.MOD_ID, "textures/entity/shocker/shocker_glow_angry.png");

    public static final Identifier MODEL = new Identifier(MineCells.MOD_ID, "geo/entity/shocker.geo.json");

    public ShockerGlowLayer(IGeoRenderer<ShockerEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, ShockerEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderLayer cameo =  RenderLayer.getEyes(entityLivingBaseIn.getAttackState().equals("none") ? LAYER_NORMAL : LAYER_ANGRY);
        matrixStackIn.push();
        matrixStackIn.scale(1.0f, 1.0f, 1.0f);
        matrixStackIn.translate(0.0d, 0.0d, 0.0d);
        this.getRenderer().render(this.getEntityModel().getModel(MODEL), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
                bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        matrixStackIn.pop();
    }
}
