package com.github.mim1q.minecells.client.render.blockentity.portal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.RiftBlockEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static com.github.mim1q.minecells.util.MathUtils.radians;
import static org.joml.Math.clamp;
import static org.joml.Math.sin;

public class RiftBlockEntityRenderer implements BlockEntityRenderer<RiftBlockEntity> {
  private static final Identifier BASE_TEXTURE = MineCells.createId("textures/blockentity/rift/base.png");
  private static final Identifier BACKGROUND_TEXTURE = MineCells.createId("textures/blockentity/rift/background.png");
  private static final String DESCRIPTION_KEY = "block.minecells.rift.description";

  private final TextRenderer textRenderer;

  public RiftBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    this.textRenderer = ctx.getTextRenderer();
  }

  @Override
  public void render(RiftBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var player = MinecraftClient.getInstance().player;
    if (player == null) return;

    matrices.push();
    matrices.translate(0.5f, 0.5f, 0.5f);
    matrices.multiply(new Quaternionf().rotationY(radians(entity.getRotation(tickDelta))));

    matrices.push();
    matrices.translate(0.0f, 0.0f, 0.001f);

    var frameConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(BASE_TEXTURE));
    RenderUtils.drawBillboard(frameConsumer, matrices, 0xf000f0, 1f, 2f, 0.0f, 0.5f, 0f, 1f, 0x80FFFFFF);
    matrices.translate(0.0f, 0.0f, -0.002f);
    RenderUtils.drawBillboard(frameConsumer, matrices, 0xf000f0, 1f, 2f, 0.0f, 0.5f, 0f, 1f, 0xFFFFFFFF);

    matrices.pop();

    var direction = new Vector3f(0, 0, 1f).rotateY(MathHelper.HALF_PI + radians(entity.getRotation(tickDelta)));
    var blockPos = Vec3d.ofCenter(entity.getPos());
    var playerVector = player.getLerpedPos(tickDelta).subtract(blockPos).normalize();
    var dot = direction.dot(playerVector.toVector3f());
    var backgroundConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(BACKGROUND_TEXTURE));
    var uOffset = sin(dot) * -8f;
    var yOffset = clamp(-5f, 5f, 1 + player.getLerpedPos(tickDelta).toVector3f().y - (float) blockPos.y) * 2f;
    var scale = 0.33f;

    drawPartialPortal(backgroundConsumer, matrices, 2, 22, 5, uOffset, yOffset, scale);
    drawPartialPortal(backgroundConsumer, matrices, 3, 28, 2, uOffset, yOffset, scale);
    drawPartialPortal(backgroundConsumer, matrices, 4, 30, -2, uOffset, yOffset, scale);
    drawPartialPortal(backgroundConsumer, matrices, 3, 28, -5, uOffset, yOffset, scale);
    drawPartialPortal(backgroundConsumer, matrices, 2, 22, -7, uOffset, yOffset, scale);

    matrices.pop();

    matrices.push();
    var text = Text.translatable(DESCRIPTION_KEY);
    matrices.translate(0.5, 2.0, 0.5);
    matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
    matrices.scale(-0.025f, -0.025f, 0.025f);
    textRenderer.draw(text, -textRenderer.getWidth(text) / 2f, 0.0F, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextLayerType.NORMAL, 0x80000000, 0xF000F0);
    matrices.pop();
  }

  private void drawPartialPortal(VertexConsumer consumer, MatrixStack matrices, int width, int height, int x, float uOffset, float vOffset, float scale) {
    var y = -height / 2;
    var startU = 0.5f + (x + uOffset) * scale / 16f;
    var startV = 0.5f + (y + vOffset) * scale / 16f;
    RenderUtils.drawBillboard(
      consumer,
      matrices,
      0xf000f0,
      x / 16f,
      (x + width) / 16f,
      -height / 32.0f,
      height / 32.0f,
      startU,
      startU + width * scale / 16f,
      startV,
      startV + height * scale / 16f,
      0xFFFFFFFF,
      OverlayTexture.DEFAULT_UV
    );
  }
}
