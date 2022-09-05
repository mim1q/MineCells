package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class KingdomPortalBlockEntityRenderer implements BlockEntityRenderer<KingdomPortalCoreBlockEntity> {

  private static final Identifier TEXTURE = MineCells.createId("textures/blockentity/kingdom_portal.png");

  private final KingdomPortalBlockEntityModel model;
  public KingdomPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    EntityModelLoader loader = ctx.getLayerRenderDispatcher();
    this.model = new KingdomPortalBlockEntityModel(loader.getModelPart(RendererRegistry.KINGDOM_PORTAL_LAYER));
  }

  @Override
  public void render(KingdomPortalCoreBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    matrices.push();
    matrices.scale(-1.0F, -1.0F, 1.0F);
    matrices.translate(0.0D, -1.0D, 0.5D);
    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
    this.model.render(matrices, vertexConsumer, 0xF000F0, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getEyes(TEXTURE));
    this.model.render(matrices, vertexConsumer2, 0xF000F0, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  public static class KingdomPortalBlockEntityModel extends Model {

    ModelPart main;

    public KingdomPortalBlockEntityModel(ModelPart root) {
      super(RenderLayer::getEntityCutoutNoCull);
      this.main = root.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();

      root.addChild("main",
        ModelPartBuilder.create()
          // Main portal
          .uv(0, 0)
          .cuboid(-20.0F, -20.0F, 0.0F, 40.0F, 40.0F, 0.0F, new Dilation(0.001F))
          // Bottom hieroglyph
          .uv(0, 48)
          .cuboid(-6.0F, 24.0F, -5.0F, 12.0F, 6.0F, 0.0F, new Dilation(0.01F))
          .cuboid(-6.0F, 24.0F, 5.0F, 12.0F, 6.0F, 0.0F, new Dilation(0.01F))
          // Top hieroglyph
          .uv(0, 64)
          .cuboid(-2.5F, -30.5F, -5.0F, 5.0F, 7.0F, 0.0F, new Dilation(0.01F))
          .cuboid(-2.5F, -30.5F, 5.0F, 5.0F, 7.0F, 0.0F, new Dilation(0.01F))
          // Top right hieroglyph
          .uv(0, 80)
          .cuboid(22.0F, -20.5F, -5.0F, 3.0F, 6.0F, 0.0F, new Dilation(0.01F))
          .cuboid(22.0F, -20.5F, 5.0F, 3.0F, 6.0F, 0.0F, new Dilation(0.01F))
          // Top left hieroglyph
          .uv(0, 96)
          .cuboid(-26.5F, -20.5F, -5.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.01F))
          .cuboid(-26.5F, -20.5F, 5.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.01F))
          // Left hieroglyph
          .uv(16, 64)
          .cuboid(-30.0F, -1.0F, -5.0F, 4.0F, 5.0F, 0.0F, new Dilation(0.01F))
          .cuboid(-30.0F, -1.0F, 5.0F, 4.0F, 5.0F, 0.0F, new Dilation(0.01F))
          // Right hieroglyph
          .uv(16, 80)
          .cuboid(26.0F, -1.0F, -5.0F, 4.0F, 6.0F, 0.0F, new Dilation(0.01F))
          .cuboid(26.0F, -1.0F, 5.0F, 4.0F, 6.0F, 0.0F, new Dilation(0.01F)),
        ModelTransform.pivot(0.0F, 0.0F, 0.0F)
      );

      return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      matrices.push();
      this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
      matrices.pop();
    }
  }
}
