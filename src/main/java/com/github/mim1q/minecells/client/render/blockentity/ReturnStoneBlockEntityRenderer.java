package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ReturnStoneBlock;
import com.github.mim1q.minecells.block.blockentity.ReturnStoneBlockEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class ReturnStoneBlockEntityRenderer implements BlockEntityRenderer<ReturnStoneBlockEntity> {
  private static final String TEXT_KEY = "block.minecells.return_stone.title";
  private static final Identifier TEXTURE = MineCells.createId("textures/block/return_stone.png");
  private final TextRenderer textRenderer;
  private final EntityRenderDispatcher dispatcher;

  public ReturnStoneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    textRenderer = ctx.getTextRenderer();
    dispatcher = ctx.getEntityRenderDispatcher();
  }

  @Override
  public void render(ReturnStoneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    matrices.push();
    matrices.scale(-1.0F, -1.0F, 1.0F);
    matrices.translate(-0.5F, -0.5F, 0.5F);
    renderArrow(matrices, vertexConsumers, entity.getCachedState().get(ReturnStoneBlock.FACING).asRotation());
    renderBall(matrices, vertexConsumers);
    renderLabel(matrices, vertexConsumers);
    matrices.pop();
  }

  private void renderArrow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float rotation) {
    matrices.push();
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
    VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityShadow(TEXTURE));
    matrices.translate(0.0F, 0.5F / 16F, 5.01F / 16F);
    Matrix3f m3f = matrices.peek().getNormalMatrix();
    Matrix4f m4f = matrices.peek().getPositionMatrix();

    float dx = 5.0F / 16.0F;
    float dy = 3.5F / 16.0F;
    RenderUtils.produceVertex(consumer, m4f, m3f, 0xF000F0, -dx, -dy, 0F, 19 / 32F, 17 / 32F, 255);
    RenderUtils.produceVertex(consumer, m4f, m3f, 0xF000F0,  dx, -dy, 0F,  9 / 32F, 17 / 32F, 255);
    RenderUtils.produceVertex(consumer, m4f, m3f, 0xF000F0,  dx,  dy, 0F,  9 / 32F, 24 / 32F, 255);
    RenderUtils.produceVertex(consumer, m4f, m3f, 0xF000F0, -dx,  dy, 0F, 19 / 32F, 24 / 32F, 255);
    matrices.pop();
  }

  private void renderBall(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
    matrices.push();
    matrices.scale(-1F, -1F, 1F);
    matrices.translate(0.0F, 0.75F, 0.0F);
    matrices.multiply(dispatcher.getRotation());
    VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
    float d = 7.0F / 16F;
    RenderUtils.drawBillboard(consumer, matrices, 0xF000F0, d, d, 1/32F, 8/32F, 17/32F, 24/32F, 255);
    matrices.pop();
  }

  private void renderLabel(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
    matrices.push();
    matrices.scale(-1F, -1F, 1F);
    matrices.translate(0.0F, 1.25F, 0.0F);
    matrices.multiply(dispatcher.getRotation());
    matrices.scale(-0.02F, -0.02F, 0.02F);
    Text text = Text.translatable(TEXT_KEY);
    float width = textRenderer.getWidth(text);
    textRenderer.draw(
      Text.translatable(TEXT_KEY),
      -width / 2.0F,
      0.0F,
      0xFFFFFF,
      false,
      matrices.peek().getPositionMatrix(),
      vertexConsumers,
      false,
      0x80000000,
      LightmapTextureManager.MAX_LIGHT_COORDINATE
    );
    matrices.pop();
  }
}
