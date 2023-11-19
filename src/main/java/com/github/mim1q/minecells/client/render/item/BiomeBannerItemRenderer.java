package com.github.mim1q.minecells.client.render.item;

import com.github.mim1q.minecells.block.FlagBlock;
import com.github.mim1q.minecells.client.render.blockentity.FlagBlockEntityRenderer.BiomeBannerBlockEntityModel;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class BiomeBannerItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
  private final BiomeBannerBlockEntityModel model;
  private final FlagBlock flagBlock;

  public BiomeBannerItemRenderer(EntityModelLoader loader, FlagBlock flagBlock) {
    this.flagBlock = flagBlock;
    this.model = new BiomeBannerBlockEntityModel(
      loader.getModelPart(flagBlock.large ? MineCellsRenderers.FLAG_LARGE_LAYER : MineCellsRenderers.FLAG_LAYER)
    );
  }

  @Override
  public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    VertexConsumer consumer = vertexConsumers.getBuffer(model.getLayer(flagBlock.texture));

    matrices.push();

    float scale = 0.4F;
    float x = 1.2F;
    float y = -2.5F;
    float z = -1.0F;

    if (mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
      y = -1.25F;
    }

    matrices.scale(scale, -scale, -scale);
    matrices.translate(x, y, z);
    model.setupLargeItemModel();
    model.render(matrices, consumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    model.resetSegments();
    matrices.pop();
  }
}
