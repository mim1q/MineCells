package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.CellEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CellEntityRenderer extends EntityRenderer<CellEntity> {

  public static final Identifier TEXTURE = MineCells.createId("textures/entity/cell.png");
  public static final RenderLayer LAYER = RenderLayer.getEntityTranslucentEmissive(TEXTURE);

  public CellEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
  }

  @Override
  public void render(CellEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
    this.renderCell(entity, tickDelta, matrixStack, vertexConsumerProvider, light, new Vector3f(0, 0, 0));
    int amount = entity.getAmount();
    if (amount > 1) {
      this.renderLabel(amount, matrixStack, vertexConsumerProvider, light);
    }
  }

  protected void renderCell(CellEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, Vector3f offset) {
    matrixStack.push();
    float deltaY = (MathHelper.sin((entity.age + tickDelta) * 0.1F + entity.getId()) + 1.0F) * 0.2F;
    matrixStack.translate(0.0F + offset.x(), 0.25F + deltaY, 0.0F + offset.z());
    matrixStack.scale(0.5F, 0.5F, 0.5F);
    matrixStack.multiply(this.dispatcher.getRotation());
    matrixStack.multiply(new Quaternionf().rotationY(MathUtils.radians(180F)));
    MatrixStack.Entry entry = matrixStack.peek();
    Matrix4f matrix4f = entry.getPositionMatrix();
    Matrix3f matrix3f = entry.getNormalMatrix();
    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 0xFF);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, 0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0xFF);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, 0xFF);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0xFF);
    matrixStack.pop();
  }

  protected void renderLabel(int amount, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
    matrixStack.push();
    TextRenderer textRenderer = this.getTextRenderer();
    matrixStack.translate(0.0F, 1.0F, 0.0F);
    matrixStack.multiply(this.dispatcher.getRotation());
    matrixStack.scale(-0.025F, -0.025F, -0.025F);
    Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
    String text = String.valueOf(amount);
    float h = (float) (-textRenderer.getWidth(text) / 2);
    textRenderer.draw(text, h, 0, 0x95D2FF, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0x000000FF, 0xF0);
    matrixStack.pop();
  }

  @Override
  public Identifier getTexture(CellEntity entity) {
    return TEXTURE;
  }
}
