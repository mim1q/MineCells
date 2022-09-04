package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class KingdomPortalBlockEntityRenderer implements BlockEntityRenderer<KingdomPortalCoreBlockEntity> {

  private static final Identifier TEXTURE = MineCells.createId("textures/blockentity/kingdom_portal.png");
  private static final Identifier TEXTURE_GLOW = MineCells.createId("textures/blockentity/kingdom_portal_glow.png");

  private final KingdomPortalBlockEntityModel model;
  public KingdomPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    EntityModelLoader loader = ctx.getLayerRenderDispatcher();
    this.model = new KingdomPortalBlockEntityModel(loader.getModelPart(RendererRegistry.KINGDOM_PORTAL_LAYER));
  }

  @Override
  public void render(KingdomPortalCoreBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    matrices.push();
    matrices.scale(1.0F, -1.0F, -1.0F); //
    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
    this.model.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    VertexConsumer vertexConsumerGlow = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE_GLOW));
    this.model.render(matrices, vertexConsumerGlow, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
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
          .uv(0, 20)
          .cuboid(-14.0F, -10.0F, -5.0F, 28, 10, 10)
          .uv(40, 40)
          .cuboid(12.0F, -14.0F, -5.0F, 11, 11, 10)
          .uv(40, 40)
          .cuboid(18.0F, -20.0F, -5.0F, 11, 11, 10),
        ModelTransform.pivot(0.0F, 24.0F, 0.0F)
      );

      return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      matrices.push();
      this.main.render(matrices, vertices, light, overlay);
      matrices.pop();
    }
  }
}
