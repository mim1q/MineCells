package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SpawnerRuneEntityRenderer extends EntityRenderer<SpawnerRuneEntity> {
  public static final Identifier TEXTURE = MineCells.createId("textures/entity/spawner_rune.png");

  public SpawnerRuneEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
  }

  @Override
  public void render(SpawnerRuneEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    if (!entity.isVisible) {
      return;
    }
    matrices.push();
    var yOffset = MathHelper.sin(0.1F * (entity.age + tickDelta)) * 0.15F;
    matrices.translate(0.0D, 1.25D + yOffset, 0.0D);
    var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
    matrices.multiply(dispatcher.getRotation());
    var consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
    RenderUtils.drawBillboard(consumer, matrices, 0xF000F0, 0.75F, 0.75F, 200);
    matrices.pop();
  }

  @Override
  public Identifier getTexture(SpawnerRuneEntity entity) {
    return TEXTURE;
  }
}
