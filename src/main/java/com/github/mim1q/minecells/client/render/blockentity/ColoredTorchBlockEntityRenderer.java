package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.block.ColoredTorchBlock;
import com.github.mim1q.minecells.block.blockentity.ColoredTorchBlockEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;

public class ColoredTorchBlockEntityRenderer implements BlockEntityRenderer<ColoredTorchBlockEntity> {
  @Override
  public void render(ColoredTorchBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    if (entity.getWorld() == null || MinecraftClient.getInstance().getCameraEntity() == null) {
      return;
    }
    matrices.push();

    matrices.translate(0.5F, 0.0F, 0.5F);
    Direction facing = entity.getCachedState().get(ColoredTorchBlock.FACING);
    matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(facing.asRotation()));
    matrices.translate(0.0F, 0.8F, -0.375F);

    Vec3d camera = MinecraftClient.getInstance().getCameraEntity().getCameraPosVec(tickDelta);
    Vec3d pos = ColoredTorchBlock.getOffsetPos(entity.getPos(), facing);
    float pitch = (float) -MathHelper.atan2(pos.getX() - camera.getX(), pos.getZ() - camera.getZ());

    matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(MathHelper.DEGREES_PER_RADIAN * pitch - facing.asRotation() - 180.0F));

    MatrixStack.Entry entry = matrices.peek();
    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(entity.getTexture()));
    int time = (int)(entity.getWorld().getTime() / 2);
    drawFlame(entry, vertexConsumer, light, time % 7);

    matrices.pop();
  }

  private void drawFlame(MatrixStack.Entry entry, VertexConsumer consumer, int light, int sprite) {
    Matrix4f m4f = entry.getPositionMatrix();
    Matrix3f m3f = entry.getNormalMatrix();
    float v = sprite * 0.125F;
    float x = -2.5F * (float) 0.0625;
    float y = 16.0F * (float) 0.0625;
    RenderUtils.produceVertex(consumer, m4f, m3f, light, -x, y, 0.0F, 0.0F, v, 127);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, x, y, 0.0F, 0.3125F, v, 127);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, x, 0.0F, 0.0F, 0.3125F, v + 0.125F, 127);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, -x, 0.0F, 0.0F, 0.0F, v + 0.125F, 127);
  }
}
