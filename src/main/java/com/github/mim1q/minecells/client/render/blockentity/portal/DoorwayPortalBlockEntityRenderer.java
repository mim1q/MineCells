package com.github.mim1q.minecells.client.render.blockentity.portal;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class DoorwayPortalBlockEntityRenderer implements BlockEntityRenderer<DoorwayPortalBlockEntity> {
  public DoorwayPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

  }

  @Override
  public void render(DoorwayPortalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(entity.getTexture()));
    matrices.push();
    matrices.translate(0.5, 0.25, 0.99);
    RenderUtils.drawBillboard(vertices, matrices, 0xF000F0, 1.5F, 2.5F, 40F/64, 64F/64, 8F/64,48F/64, 255);
    var time = entity.getWorld().getTime() + tickDelta;
    var barsProgress = ((float)Math.cos(time * 0.1F) * 0.5F) + 0.5F;
    var minY = 1.25F - barsProgress * 2.5F;
    var minV = (48 - 40F * barsProgress)/64;
    matrices.translate(0.0, 0.01, -0.25);
    RenderUtils.drawBillboard(vertices, matrices, light, -0.75F, 0.75F, minY, 1.25F, 16F/64, 40F/64, minV, 48F/64, 255);
    matrices.pop();
  }
}
