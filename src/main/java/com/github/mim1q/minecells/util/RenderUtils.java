package com.github.mim1q.minecells.util;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class RenderUtils {
  public static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, float y, float z, float textureU, float textureV, int alpha) {
    vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, alpha).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
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
