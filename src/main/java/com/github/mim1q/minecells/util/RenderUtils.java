package com.github.mim1q.minecells.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public class RenderUtils {
  public static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, float y, float z, float textureU, float textureV, int alpha) {
    vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, alpha).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
  }

  public static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, int argb, float x, float y, float z, float textureU, float textureV) {
    vertexConsumer.vertex(positionMatrix, x, y, z).color(argb).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
  }

  public static void drawBillboard(VertexConsumer consumer, MatrixStack matrices, int light, float width, float height, int argb) {
    drawBillboard(consumer, matrices, light, width, height, 0.0F, 1.0F, 0.0F, 1.0F, argb);
  }

  public static void drawBillboard(VertexConsumer consumer, MatrixStack matrices, int light, float width, float height, float minU, float maxU, float minV, float maxV, int argb) {
    float dx = width / 2F;
    float dy = height / 2F;

    drawBillboard(consumer, matrices, light, -dx, dx, -dy, dy, minU, maxU, minV, maxV, argb);
  }

  public static float getGlobalAnimationProgress() {
    return MinecraftClient.getInstance().inGameHud.getTicks() + MinecraftClient.getInstance().getTickDelta();
  }

  public static void drawBillboard(VertexConsumer consumer, MatrixStack matrices, int light, float minX, float maxX, float minY, float maxY, float minU, float maxU, float minV, float maxV, int argb) {
    Matrix3f m3f = matrices.peek().getNormalMatrix();
    Matrix4f m4f = matrices.peek().getPositionMatrix();

    RenderUtils.produceVertex(consumer, m4f, m3f, light, argb, minX, maxY, 0F, minU, minV);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, argb, maxX, maxY, 0F, maxU, minV);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, argb, maxX, minY, 0F, maxU, maxV);
    RenderUtils.produceVertex(consumer, m4f, m3f, light, argb, minX, minY, 0F, minU, maxV);
  }

  public static void renderBakedModel(BakedModel model, Random random, int light, MatrixStack matrices, VertexConsumer buffer) {
    for (var direction : Direction.values()) {
      var quads = model.getQuads(null, direction, random);
      renderBakedQuads(quads, matrices, buffer, light);
    }

    var noDirectionQuads = model.getQuads(null, null, random);
    renderBakedQuads(noDirectionQuads, matrices, buffer, light);
  }

  private static void renderBakedQuads(List<BakedQuad> quads, MatrixStack matrices, VertexConsumer buffer, int light) {
    for (var quad : quads) {
      buffer.quad(
        matrices.peek(),
        quad,
        new float[]{1.0F, 1.0F, 1.0F, 1.0F},
        1f, 1f, 1f,
        new int[]{light, light, light, light},
        OverlayTexture.DEFAULT_UV,
        false
      );
    }
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
