package com.github.mim1q.minecells.client.render.blockentity.portal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.portal.TeleporterBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TeleporterBlockEntityRenderer implements BlockEntityRenderer<TeleporterBlockEntity> {
  private static final Identifier TEXTURE = MineCells.createId("textures/blockentity/teleporter.png");
  private final TeleporterModel model;

  public TeleporterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    model = new TeleporterModel(ctx.getLayerModelPart(MineCellsRenderers.TELEPORTER_LAYER));
  }

  @Override
  public void render(TeleporterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
    matrices.push();
    matrices.scale(-1.0F, -1.0F, 1.0F);
    matrices.translate(0.0F, -0.5F, 0.5F);
    model.render(matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  public static class TeleporterModel extends Model {
    private final ModelPart main;

    public TeleporterModel(ModelPart root) {
      super(RenderLayer::getEntityCutout);
      this.main = root.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      ModelPartData dMain = modelPartData.addChild("main", ModelPartBuilder.create()
        .uv(0, 20).cuboid(-12.0F, -64.0F, -5.0F, 24.0F, 10.0F, 10.0F, new Dilation(0.01F))
        .uv(0, 0).cuboid(-12.0F, -10.0F, -5.0F, 24.0F, 10.0F, 10.0F, new Dilation(0.01F))
        .uv(58, 10).cuboid(-22.0F, -14.0F, -5.0F, 12.0F, 10.0F, 10.0F, new Dilation(0.005F))
        .uv(88, 52).cuboid(-28.0F, -22.0F, -5.0F, 10.0F, 12.0F, 10.0F, new Dilation(0.0F))
        .uv(0, 40).cuboid(-32.0F, -44.0F, -5.0F, 10.0F, 24.0F, 10.0F, new Dilation(0.005F))
        .uv(88, 30).cuboid(-28.0F, -54.0F, -5.0F, 10.0F, 12.0F, 10.0F, new Dilation(0.0F))
        .uv(40, 40).cuboid(-22.0F, -60.0F, -5.0F, 12.0F, 10.0F, 10.0F, new Dilation(0.005F)),
        ModelTransform.pivot(0.0F, 24.0F, 0.0F));
      dMain.addChild("main_rotated", ModelPartBuilder.create()
        .uv(40, 40).cuboid(-22.0F, -36.0F, -5.0F, 12.0F, 10.0F, 10.0F, new Dilation(0.005F))
        .uv(88, 30).cuboid(-28.0F, -30.0F, -5.0F, 10.0F, 12.0F, 10.0F, new Dilation(0.0F))
        .uv(0, 40).cuboid(-32.0F, -20.0F, -5.0F, 10.0F, 24.0F, 10.0F, new Dilation(0.005F))
        .uv(88, 52).cuboid(-28.0F, 2.0F, -5.0F, 10.0F, 12.0F, 10.0F, new Dilation(0.0F))
        .uv(58, 10).cuboid(-22.0F, 10.0F, -5.0F, 12.0F, 10.0F, 10.0F, new Dilation(0.005F)),
        ModelTransform.of(0.0F, -24.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));
      dMain.addChild("pillar_5", ModelPartBuilder.create().uv(104, 0).cuboid(-3.0F, -7.5F, -3.0F, 6.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(24.0F, -6.5F, 9.0F, 0.0F, -2.7925F, 0.0F));
      dMain.addChild("pillar_4", ModelPartBuilder.create().uv(104, 0).cuboid(0.0F, -10.5F, 0.0F, 6.0F, 17.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(30.0F, -6.5F, -6.0F, 0.0F, -0.2618F, 0.0F));
      dMain.addChild("pillar_3", ModelPartBuilder.create().uv(104, 0).cuboid(-3.0F, -10.5F, -3.0F, 6.0F, 18.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(17.0F, -7.5F, -8.0F, 0.0F, -1.5708F, 0.0F));
      dMain.addChild("pillar_2", ModelPartBuilder.create().uv(104, 0).cuboid(-3.0F, -10.5F, -3.0F, 6.0F, 16.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-24.0F, -5.5F, -9.0F, 0.0F, -0.5236F, 0.0F));
      dMain.addChild("pillar_1", ModelPartBuilder.create().uv(104, 0).cuboid(-3.0F, -10.5F, -3.0F, 6.0F, 17.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-32.0F, -6.5F, 5.0F, 0.0F, 1.0472F, 0.0F));
      dMain.addChild("pillar_0", ModelPartBuilder.create().uv(104, 0).cuboid(-3.0F, -10.5F, -3.0F, 6.0F, 21.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-20.0F, -10.5F, 9.0F, 0.0F, 1.5708F, 0.0F));
      return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
  }
}
