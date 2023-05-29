package com.github.mim1q.minecells.util;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class RenderUtils {
  public static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, float y, float z, float textureU, float textureV, int alpha) {
    vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, alpha).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
  }

  public static void drawBillboard(VertexConsumer consumer, MatrixStack matrices, int light, float width, float height, int alpha) {
    drawBillboard(consumer, matrices, light, width, height, 0.0F, 1.0F, 0.0F, 1.0F, alpha);
  }

  public static void drawBillboard(VertexConsumer consumer, MatrixStack matrices, int light, float width, float height, float minU, float maxU, float minV, float maxV, int alpha) {
    float dx = width / 2F;
    float dy = height / 2F;

    drawBillboard(consumer, matrices, light, -dx, dx, -dy, dy, minU, maxU, minV, maxV, alpha);
  }

  public static void drawBillboard(VertexConsumer consumer, MatrixStack matrices, int light, float minX, float maxX, float minY, float maxY, float minU, float maxU, float minV, float maxV, int alpha) {
    Matrix3f m3f = matrices.peek().getNormalMatrix();
    Matrix4f m4f = matrices.peek().getPositionMatrix();

    RenderUtils.produceVertex(consumer, m4f, m3f, light, minX, maxY, 0F, minU, minV, alpha);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, maxX, maxY, 0F, maxU, minV, alpha);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, maxX, minY, 0F, maxU, maxV, alpha);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, minX, minY, 0F, minU, maxV, alpha);
  }

  public static class VertexCoordinates {
    public float x;
    public float y;
    public float z;
    public float u;
    public float v;

    public VertexCoordinates(float x, float y, float z, float u, float v) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.u = u;
      this.v = v;
    }
  }
}
