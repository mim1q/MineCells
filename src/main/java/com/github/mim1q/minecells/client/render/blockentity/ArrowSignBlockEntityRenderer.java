package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ArrowSignBlock;
import com.github.mim1q.minecells.block.blockentity.ArrowSignBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class ArrowSignBlockEntityRenderer implements BlockEntityRenderer<ArrowSignBlockEntity> {
  private static final Identifier TEXTURE = MineCells.createId("textures/blockentity/arrow_sign.png");
  private final ModelPart root;
  private final ItemRenderer itemRenderer;
  private final BlockRenderManager blockRenderer;

  public ArrowSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    root = ctx.getLayerModelPart(MineCellsRenderers.ARROW_SIGN_LAYER);
    itemRenderer = ctx.getItemRenderer();
    blockRenderer = ctx.getRenderManager();
  }

  @Override
  public void render(ArrowSignBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var state = entity.getCachedState();
    var chainState = entity.getChainState();
    matrices.push();
    {
      blockRenderer.renderDamage(chainState, entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()));

      matrices.scale(1F, -1F, -1F);
      matrices.translate(0.5F, -1.5F, -0.5F);

      var rotation = state.get(ArrowSignBlock.ROTATION) * 22.5F;
      matrices.multiply(new Quaternionf().rotationY(MathUtils.radians(rotation)));
      var vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
      if (state.get(ArrowSignBlock.MIDDLE)) {
        matrices.translate(0F, -3 / 16f, -6 / 16f);
      }

      matrices.translate(0f, 1f, 0f);
      var verticalRotation = entity.getVerticalRotation();
      var verticalRotationDegrees = verticalRotation * 22.5F;
      var flipRotation = (verticalRotation >= 4 && verticalRotation <= 11);
      if (flipRotation) verticalRotationDegrees -= 180F;

      matrices.multiply(new Quaternionf().rotationZ(MathUtils.radians(verticalRotationDegrees)));
      matrices.translate(0f, -1f, 0f);
      if (flipRotation) {
        matrices.translate(0f, 0f, 6 / 16f);
        matrices.multiply(new Quaternionf().rotationY(MathUtils.radians(180F)));
        matrices.translate(0f, 0f, -6 / 16f);
      }
      root.render(matrices, vertices, light, overlay);
      matrices.push();
      {
        matrices.translate(0F, 1.125F, 3.99f / 16f);
        renderIcon(entity, matrices, vertexConsumers, light, overlay);
        matrices.multiply(new Quaternionf().rotationY(MathUtils.radians(180F)));
        matrices.translate(0F, 0F, -4.02 / 16f);
        renderIcon(entity, matrices, vertexConsumers, light, overlay);
      }
      matrices.pop();
    }
    matrices.pop();
  }

  private void renderIcon(ArrowSignBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var itemStack = entity.getItemStack();
    if (!itemStack.isEmpty()) {
      matrices.push();
      matrices.scale(-0.5F, -0.5F, 0.5F);
      matrices.translate(0.0F, 0.33F, 0.0F);
      itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, null, 0);
      matrices.pop();
    }
  }

  public static TexturedModelData getTexturedModelData() {
    var modelData = new ModelData();
    var modelPartData = modelData.getRoot();
    var main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-15.5F, -13.0F, 4.0F, 27.0F, 10.0F, 4.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
    main.addChild("cube_r1", ModelPartBuilder.create().uv(0, 14).cuboid(-5.275F, -5.275F, -3.0F, 7.0F, 7.0F, 4.0F, new Dilation(0.05F)), ModelTransform.of(-8.0F, -8.0F, 7.0F, 0.0F, 0.0F, -0.7854F));
    return TexturedModelData.of(modelData, 64, 64);
  }
}
