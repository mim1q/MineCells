package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

public interface SpawnerRuneRenderer {
  Identifier TEXTURE = MineCells.createId("textures/block/spawner_rune.png");

  private static void render(
    SpawnerRuneController controller,
    BlockState state,
    BlockPos pos,
    MatrixStack matrices,
    VertexConsumerProvider vertices
  ) {
    if (!controller.isVisible()) return;

    matrices.push();
    var age = RenderUtils.getGlobalAnimationProgress();
    var yOffset = 0.5 + Math.sin(0.1F * age) * 0.15F;
    matrices.scale(0.75f, 0.75f, 0.75f);
    matrices.translate(0.0, yOffset, 0.0);
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age + pos.hashCode()));
    matrices.translate(-0.5, 0.0, -0.5);

    var world = MinecraftClient.getInstance().world;
    if (world == null) {
      matrices.pop();
      return;
    }

    var blockRenderer = MinecraftClient.getInstance().getBlockRenderManager();
    var model = blockRenderer.getModel(state);
    blockRenderer.getModelRenderer().render(
      world, model, state, pos, matrices, vertices.getBuffer(RenderLayer.getTranslucent()), true, world.getRandom().split(), 0, OverlayTexture.DEFAULT_UV
    );
    matrices.pop();
  }

  final class Entity extends EntityRenderer<SpawnerRuneEntity> implements SpawnerRuneRenderer {
    public Entity(EntityRendererFactory.Context ctx) {
      super(ctx);
    }

    @Override
    public void render(SpawnerRuneEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      if (!entity.controller.isVisible()) return;
      SpawnerRuneRenderer.render(entity.controller, MineCellsBlocks.SPAWNER_RUNE.getDefaultState(), entity.getBlockPos(), matrices, vertexConsumers);
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
      SpawnerRuneRenderer.render(entity.controller, entity.getCachedState(), entity.getPos(), matrices, vertexConsumers);
      matrices.pop();
    }
  }
}
