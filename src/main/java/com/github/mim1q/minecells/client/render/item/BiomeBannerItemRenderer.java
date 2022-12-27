package com.github.mim1q.minecells.client.render.item;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.blockentity.BiomeBannerBlockEntityRenderer.BiomeBannerBlockEntityModel;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class BiomeBannerItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

  private final BiomeBannerBlockEntityModel model;

  public BiomeBannerItemRenderer(EntityModelLoader loader) {
    this.model = new BiomeBannerBlockEntityModel(loader.getModelPart(MineCellsRenderers.BIOME_BANNER_LAYER));
  }

  @Override
  public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    NbtCompound nbt = stack.getNbt();
    String name = nbt == null ? "king_crest" : nbt.getString("pattern");
    VertexConsumer consumer = vertexConsumers.getBuffer(model.getLayer(MineCells.createId("textures/blockentity/banner/" + name + ".png")));

    matrices.push();

    float scale = 0.45F;
    float x = 1.15F;
    float y = -2.25F;
    float z = -1.0F;

    if (mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND) {
      y = -1.25F;
    }

    matrices.scale(scale, -scale, -scale);
    matrices.translate(x, y, z);
    this.model.render(matrices, consumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

    matrices.pop();
  }
}
