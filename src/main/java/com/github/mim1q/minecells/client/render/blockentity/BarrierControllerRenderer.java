package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.block.ConditionalBarrierBlock;
import com.github.mim1q.minecells.block.blockentity.BarrierControllerBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class BarrierControllerRenderer implements BlockEntityRenderer<BarrierControllerBlockEntity> {
  private final Identifier TEXTURE = new Identifier("minecells", "textures/blockentity/big_door.png");
  private final BarrierControllerModel model;

  public BarrierControllerRenderer(BlockEntityRendererFactory.Context ctx) {
    var loader = ctx.getLayerRenderDispatcher();
    this.model = new BarrierControllerModel(loader.getModelPart(MineCellsRenderers.BIG_DOOR_BARRIER_LAYER));
  }

  @Override
  public void render(BarrierControllerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var vertices = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
    entity.openProgress.update(entity.getWorld() == null ? 0 : entity.getWorld().getTime() + tickDelta);
    matrices.push();
    matrices.translate(0.5F, 0F, 0.5F);
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180F - entity.getCachedState().get(ConditionalBarrierBlock.FACING).asRotation()));
    model.setAngles(entity.openProgress.getValue());
    model.render(matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  public static class BarrierControllerModel extends Model {
    private final ModelPart root;

    public BarrierControllerModel(ModelPart root) {
      super(RenderLayer::getEntityCutout);
      this.root = root.getChild("root");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      matrices.push();
      matrices.scale(-1F, -1F, 1F);
      matrices.translate(0F, -1.5F, 0F);
      root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
      matrices.pop();
    }

    public void setAngles(float openProgress) {
      root.setAngles(0F, MathUtils.radians(-85F) * openProgress, 0F);
    }

    public static TexturedModelData getBigDoorTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("root", ModelPartBuilder.create()
        .uv(0, 0).cuboid(0.0F, -44.0F, -5.0F, 32.0F, 18.0F, 4.0F)
        .uv(0, 54).cuboid(0.0F, -4.0F, -6.0F, 32.0F, 4.0F, 6.0F)
        .uv(0, 64).cuboid(0.0F, -26.0F, -6.0F, 32.0F, 4.0F, 6.0F)
        .uv(72, 0).cuboid(14.0F, -28.0F, -7.0F, 4.0F, 8.0F, 8.0F)
        .uv(96, 0).cuboid(12.0F, -22.0F, 0.0F, 8.0F, 8.0F, 0.0F)
        .uv(96, 0).cuboid(12.0F, -22.0F, -6.0F, 8.0F, 8.0F, 0.0F)
        .uv(0, 44).cuboid(0.0F, -48.0F, -6.0F, 32.0F, 4.0F, 6.0F)
        .uv(0, 22).cuboid(0.0F, -22.0F, -5.0F, 32.0F, 18.0F, 4.0F),
        ModelTransform.pivot(-8.0F, 24.0F, 3.0F));
      return TexturedModelData.of(modelData, 128, 128);
    }
  }
}
