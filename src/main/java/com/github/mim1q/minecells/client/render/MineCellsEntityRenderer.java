package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

import static com.github.mim1q.minecells.util.MathUtils.radians;

public abstract class MineCellsEntityRenderer<T extends MineCellsEntity, M extends EntityModel<T>> extends MobEntityRenderer<T, M> {
  private static final Identifier RED_STAR_TEXTURE = MineCells.createId("textures/entity/effect/red_star.png");
  private static final String ELITE_KEY = "entity.minecells.elite";

  public MineCellsEntityRenderer(EntityRendererFactory.Context context, M entityModel, float f) {
    super(context, entityModel, f);
  }

  @Override
  public void render(T mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
    super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    if (mobEntity.isElite()) {
      renderEliteLabel(mobEntity, matrixStack, vertexConsumerProvider);
    }
  }

  private void renderEliteLabel(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
    matrices.push();
    {
      final var vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(RED_STAR_TEXTURE));
      final var text = Text.translatable(ELITE_KEY);

      matrices.translate(0.0D, entity.getHeight() + 0.66D, 0.0D);
      final var textScale = 1 / 48F;
      matrices.scale(textScale, -textScale, -textScale);
      matrices.translate(0.0D, -1.5D, 0.0D);
      final var camera = MinecraftClient.getInstance().gameRenderer.getCamera();
      matrices.multiply(new Quaternionf()
        .rotationY(radians(180F + camera.getYaw()))
        .rotateX(-radians(camera.getPitch()))
      );
      getTextRenderer().drawWithOutline(
        text.asOrderedText(),
        -getTextRenderer().getWidth(text) / 2.0F,
        0.0F,
        0xffc755,
        0x6d3100,
        matrices.peek().getPositionMatrix(),
        vertexConsumers,
        0xF000F0
      );

      matrices.translate(-1.0, -6.0, 0.1D);
      matrices.scale(-1.0F, -1.0F, 1.0F);
      RenderUtils.drawBillboard(vertices, matrices, 0xF000F0, 24F, 24F, 0xFFFFFFFF);
    }
    matrices.pop();
  }

  @Override
  protected void scale(T entity, MatrixStack matrices, float amount) {
    super.scale(entity, matrices, amount);
    if (entity.isElite()) {
      final var scale = MineCellsEntity.ELITE_SCALE;
      matrices.scale(scale, scale, scale);
    }
  }
}
