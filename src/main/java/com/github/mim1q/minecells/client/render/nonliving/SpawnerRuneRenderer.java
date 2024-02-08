package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public interface SpawnerRuneRenderer {
  Identifier TEXTURE = MineCells.createId("textures/entity/spawner_rune.png");

  private static void render(MatrixStack matrices, VertexConsumerProvider vertices) {
    matrices.push();
    var age = RenderUtils.getGlobalAnimationProgress();
    var yOffset = 0.5 + Math.sin(0.1F * age) * 0.15F;
    matrices.translate(0.0D, yOffset, 0.0D);
    var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
    matrices.multiply(dispatcher.getRotation());
    var consumer = vertices.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
    RenderUtils.drawBillboard(consumer, matrices, 0xF000F0, 0.75F, 0.75F, 0xC8FFFFFF);
    matrices.pop();
  }

  final class Entity extends EntityRenderer<SpawnerRuneEntity> implements SpawnerRuneRenderer {
    public Entity(EntityRendererFactory.Context ctx) {
      super(ctx);
    }

    @Override
    public void render(SpawnerRuneEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      if (!entity.controller.isVisible()) return;
      SpawnerRuneRenderer.render(matrices, vertexConsumers);
    }

    @Override
    public Identifier getTexture(SpawnerRuneEntity entity) {
      return TEXTURE;
    }
  }

  final class BlockEntity implements SpawnerRuneRenderer, BlockEntityRenderer<SpawnerRuneBlockEntity> {
    public BlockEntity(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(SpawnerRuneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
      var world = entity.getWorld();
      if (world == null || !entity.controller.isVisible()) return;
      matrices.push();
      matrices.translate(0.5, 0.0, 0.5);
      SpawnerRuneRenderer.render(matrices, vertexConsumers);
      matrices.pop();
    }
  }
}
