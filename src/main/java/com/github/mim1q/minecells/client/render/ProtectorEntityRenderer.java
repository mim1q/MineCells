package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.ProtectorEntityModel;
import com.github.mim1q.minecells.entity.ProtectorEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import com.github.mim1q.minecells.util.RenderUtils;
import com.github.mim1q.minecells.util.RenderUtils.VertexCoordinates;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class ProtectorEntityRenderer extends MobEntityRenderer<ProtectorEntity, ProtectorEntityModel> {

  private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/protector/protector.png");
  private static final Identifier GLOW_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/protector/protector_glow.png");
  private static final Identifier CONNECTION_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/misc/electric_arch.png");
  private static final RenderLayer CONNECTION_LAYER = RenderLayer.getEntityCutout(CONNECTION_TEXTURE);

  public ProtectorEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, new ProtectorEntityModel(ctx.getPart(RendererRegistry.PROTECTOR_LAYER)), 0.35F);
    if (MineCellsClient.CLIENT_CONFIG.rendering.protectorGlow) {
      this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
    }
  }

  @Override
  public void render(ProtectorEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
    super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    MatrixStack.Entry entry = matrixStack.peek();
    Matrix4f matrix4f = entry.getPositionMatrix();
    Matrix3f matrix3f = entry.getNormalMatrix();

    if (mobEntity.isActive()) {
      for (Entity e : mobEntity.trackedEntities) {
        renderConnection(
          vertexConsumerProvider.getBuffer(CONNECTION_LAYER),
          matrix4f,
          matrix3f,
          new Vec3d(0.0D, 1.25D, 0.0D),
          e.getPos().subtract(mobEntity.getPos()).add(0.0D, e.getHeight() * 0.5D, 0.0D),
          (e.age) % 8,
          i
        );
      }
    }
  }

  protected void renderConnection(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, Vec3d p0, Vec3d p1, int frame, int light) {
    float x0 = (float) p0.x;
    float y0 = (float) p0.y;
    float z0 = (float) p0.z;
    float x1 = (float) p1.x;
    float y1 = (float) p1.y;
    float z1 = (float) p1.z;

    float dx = x1 - x0;
    float dy = y1 - y0;
    float dz = z1 - z0;

    dx = dx == 0 ? 0.001F : dx;

    float dHorizontal = MathHelper.sqrt(dx * dx + dz * dz);
    float length = MathHelper.sqrt(dHorizontal * dHorizontal + dy * dy);

    float offset = 0.5F;

    float yOffset = offset * (dHorizontal / length);
    float xOffset = offset * (dy / length) * (dx / dHorizontal);
    float zOffset = offset * (dy / length) * (dz / dHorizontal);

    float v0 = frame * 0.125F;
    float v1 = v0 + 0.125F;

    VertexCoordinates[] vertices = {
      new VertexCoordinates(x0 + xOffset, y0 - yOffset, z0 + zOffset, 0.0F, v1),
      new VertexCoordinates(x1 + xOffset, y1 - yOffset, z1 + zOffset, 1.0F, v1),
      new VertexCoordinates(x1 - xOffset, y1 + yOffset, z1 - zOffset, 1.0F, v0),
      new VertexCoordinates(x0 - xOffset, y0 + yOffset, z0 - zOffset, 0.0F, v0),
    };

    int[] indices = {0, 1, 2, 3, 3, 2, 1, 0};
    for (int i : indices) {
      RenderUtils.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 0xF0, vertices[i].x, vertices[i].y, vertices[i].z, vertices[i].u, vertices[i].v, 255);
    }
  }

  @Override
  public Identifier getTexture(ProtectorEntity entity) {
    return TEXTURE;
  }
}
