package com.github.mim1q.minecells.client.render.blockentity.portal;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.List;

public class DoorwayPortalBlockEntityRenderer implements BlockEntityRenderer<DoorwayPortalBlockEntity> {
  private final TextRenderer textRenderer;

  public DoorwayPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    textRenderer = ctx.getTextRenderer();
  }

  @Override
  public void render(DoorwayPortalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(entity.getTexture()));
    matrices.push();
    matrices.translate(0.5, 0.25, 0.5);
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - entity.getRotation()));
    matrices.translate(0.0, 0.0, 0.49);
    RenderUtils.drawBillboard(vertices, matrices, 0xF000F0, 1.5F, 2.5F, 40F/64, 64F/64, 8F/64,48F/64, 255);
    var barsProgress = entity.canPlayerEnter(MinecraftClient.getInstance().player) ? 0.25F : 1.0F;
    var minY = 1.25F - barsProgress * 2.5F;
    var minV = (48 - 40F * barsProgress)/64;
    matrices.translate(0.0, 0.01, -0.25);
    RenderUtils.drawBillboard(vertices, matrices, light, -0.75F, 0.75F, minY, 1.25F, 16F/64, 40F/64, minV, 48F/64, 255);
    var text = entity.getLabel();
    renderLabel(text, matrices, vertexConsumers);
    matrices.pop();
  }

  protected void renderLabel(
    List<MutableText> text,
    MatrixStack matrices,
    VertexConsumerProvider vertexConsumers
  ) {
    matrices.push();
    matrices.translate(0.0, 2.25F, 0.0);
    matrices.scale(-0.025f, -0.025f, 0.025f);
    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
    var y = 0;
    for (MutableText line : text) {
      float h = -textRenderer.getWidth(line) / 2.0F;
      int color = 0xFFFFFFFF;
      int background = 0x80000000;
      textRenderer.draw(line, h, y, color, false, matrix4f, vertexConsumers, false, background, 0xF000F0);
      y += 10;
    }
    matrices.pop();
  }
}
