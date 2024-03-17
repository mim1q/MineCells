package com.github.mim1q.minecells.client.render.blockentity.portal;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

import static java.lang.Math.*;
import static org.joml.Math.clamp;

public class DoorwayPortalBlockEntityRenderer implements BlockEntityRenderer<DoorwayPortalBlockEntity> {
  private final TextRenderer textRenderer;

  public DoorwayPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    textRenderer = ctx.getTextRenderer();
  }

  @Override
  public void render(DoorwayPortalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    matrices.push();
    matrices.translate(0.5, 0.25, 0.5);
    matrices.multiply(new Quaternionf().rotationY(MathUtils.radians(180F - entity.getRotation())));

    var backgroundVertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(entity.getBackgroundTexture()));
    matrices.translate(0.0, 0.0, 0.49);
    renderBackground(matrices, backgroundVertices, entity);

    var foregroundVertices = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(entity.getTexture()));
    matrices.translate(0.0, 0.0, -0.01);
    RenderUtils.drawBillboard(foregroundVertices, matrices, 0xF000F0, 1.5F, 2.5F, 104F / 128, 1F, 0, 40F / 128, 0xFFFFFFFF);

    var barsProgress = entity.canPlayerEnter(MinecraftClient.getInstance().player) ? 0.25F : 1.0F;
    var minY = 1.25F - barsProgress * 2.5F;
    var minV = (40 - 40F * barsProgress) / 128;

    var barsVertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(entity.getTexture()));
    matrices.translate(0.0, 0.01, -0.25);
    RenderUtils.drawBillboard(barsVertices, matrices, light, -0.75F, 0.75F, minY, 1.25F, 80F / 128, 104F / 128, minV, 40F / 128, 0xFFFFFFFF);

    var text = entity.getLabel();
    renderLabel(text, matrices, vertexConsumers, shouldShowPosition(entity.getWorld(), entity.getPos(), entity.getCachedState().getBlock()));
    matrices.pop();
  }

  private void renderBackground(MatrixStack matrices, VertexConsumer vertexConsumers, DoorwayPortalBlockEntity entity) {
    var player = MinecraftClient.getInstance().player;

    var bgCenterV = 0.5f;
    var bgCenterU = 0.5f;

    if (player != null) {
      var pos = Vec3d.ofCenter(entity.getPos());
      var direction = new Vec3d(0, 0, 1).rotateY(MathUtils.radians(90F - entity.getRotation()));
      var playerPos = player.getLerpedPos(MinecraftClient.getInstance().getTickDelta());
      var playerVector = playerPos.subtract(pos);
      var playerVectorLength = playerVector.length();
      playerVector = playerVector.normalize().multiply(min(playerVectorLength * 0.33F, 1.0));

      var dot = (float) direction.dotProduct(playerVector) * 1 / 8F;
      var bgCenterUOffset = MathUtils.easeInOutQuad(-1, 1, (float) (sin(dot) + 1) / 2f);

      bgCenterU += clamp(-40 / 128f, 40 / 128f, bgCenterUOffset);

      var playerHeightDiff = (float) (playerPos.y - pos.y) + 1.5f;
      playerVectorLength = max(playerVectorLength, 0.9);
      bgCenterV += clamp(-24 / 128f, 24 / 128f, playerHeightDiff * 0.1F * 1 / (float) playerVectorLength);
    }

    RenderUtils.drawBillboard(
      vertexConsumers,
      matrices,
      0xF000F0,
      1.5F, 2.5F,
      bgCenterU - 24 / 128f, bgCenterU + 24 / 128f,
      bgCenterV - 40 / 128f, bgCenterV + 40 / 128f,
      0xFFFFFFFF
    );
  }

  private boolean shouldShowPosition(World world, BlockPos doorwayPos, Block block) {
    if (world == null) return false;
    if (block == MineCellsBlocks.OVERWORLD_DOORWAY) return false;
    if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult blockHit && blockHit.getType() != HitResult.Type.MISS) {
      var blockHitPos = blockHit.getBlockPos();
      var state = world.getBlockState(blockHit.getBlockPos());
      if (!(state.getBlock() instanceof DoorwayPortalBlock.Frame || state.isOf(block))) return false;
      return
        abs(blockHitPos.getX() - doorwayPos.getX()) <= 1
          && abs(blockHitPos.getY() - doorwayPos.getY()) <= 1
          && abs(blockHitPos.getZ() - doorwayPos.getZ()) <= 1;
    }
    return false;
  }

  protected void renderLabel(
    List<MutableText> text,
    MatrixStack matrices,
    VertexConsumerProvider vertexConsumers,
    boolean showPosition
  ) {
    matrices.push();
    matrices.translate(0.0, 2.5F, 0.0);
    matrices.scale(-0.025f, -0.025f, 0.025f);
    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
    var y = 15;
    var length = text.size();
    for (var i = length - 1; i >= 0; --i) {
      if (i < length - 1 || showPosition) {
        var line = text.get(i);
        float h = -textRenderer.getWidth(line) / 2.0F;
        int color = 0xFFFFFFFF;
        int background = 0x80000000;
        textRenderer.draw(line, h, y, color, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, background, 0xF000F0);
      }
      y -= 10;
    }
    matrices.pop();
  }
}
