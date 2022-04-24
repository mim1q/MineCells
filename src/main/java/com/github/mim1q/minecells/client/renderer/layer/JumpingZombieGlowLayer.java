package com.github.mim1q.minecells.client.renderer.layer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.GrenadierEntity;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import com.github.mim1q.minecells.entity.ShockerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class JumpingZombieGlowLayer extends GeoLayerRenderer<JumpingZombieEntity> {

    public static final Identifier LAYER = new Identifier(MineCells.MOD_ID, "textures/entity/jumping_zombie/jumping_zombie_glow.png");
    public static final Identifier MODEL = new Identifier(MineCells.MOD_ID, "geo/entity/jumping_zombie.geo.json");

    public JumpingZombieGlowLayer(IGeoRenderer<JumpingZombieEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, JumpingZombieEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderLayer cameo =  RenderLayer.getEyes(LAYER);
        this.getRenderer().render(this.getEntityModel().getModel(MODEL), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
                bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
    }
}
