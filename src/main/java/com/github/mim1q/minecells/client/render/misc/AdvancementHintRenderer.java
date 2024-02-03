package com.github.mim1q.minecells.client.render.misc;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AdvancementHintRenderer {
  private static final ConcurrentMap<Identifier, Boolean> RENDER_ADVANCEMENT_MAP = new ConcurrentHashMap<>();
  private static final Identifier TEXTURE = MineCells.createId("textures/misc/hint_marker.png");

  private final Identifier advancementId;
  private final ItemRenderer itemRenderer;
  private final int color;
  private final Item item;

  public AdvancementHintRenderer(Identifier advancementId, ItemRenderer itemRenderer, int argb, @Nullable ItemConvertible item) {
    this.advancementId = advancementId;
    this.itemRenderer = itemRenderer;
    this.color = argb;
    this.item = item == null ? null : item.asItem();

    if (advancementId != null) {
      RENDER_ADVANCEMENT_MAP.putIfAbsent(advancementId, true);
    }
  }

  public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float animationProgress) {
    var shouldRender = advancementId == null || RENDER_ADVANCEMENT_MAP.getOrDefault(advancementId, true);
    if (!shouldRender) return;
    matrixStack.push();
    {
      var vertices = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(TEXTURE));

      var rotationAngle = MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation()
        .getEulerAnglesYXZ(new Vector3f())
        .y;

      matrixStack.multiply(new Quaternionf().rotationY(rotationAngle));

      matrixStack.translate(-1 / 16f, 0.25f, 0f);
      var yOffset = Math.sin(animationProgress * 0.33f) * 0.05;
      matrixStack.translate(0f, yOffset, 0f);
      RenderUtils.drawBillboard(vertices, matrixStack, 0xF000F0, 1f, 1f, color);

      if (item != null) {
        matrixStack.translate(1 / 16f, 0.5f - yOffset * 0.25f, 0.0f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        itemRenderer.renderItem(item.getDefaultStack(), ModelTransformationMode.FIXED, 0xF000F0, 0xFFFFFFFF, matrixStack, vertexConsumerProvider, null, 0);
      }
    }
    matrixStack.pop();
  }

  public static void resetAdvancements() {
    RENDER_ADVANCEMENT_MAP.replaceAll((id, b) -> true);
    var player = MinecraftClient.getInstance().player;
    if (player == null) return;
    player.networkHandler.getAdvancementHandler().getManager().getAdvancements().forEach(advancement -> {
      RENDER_ADVANCEMENT_MAP.putIfAbsent(advancement.getId(), true);
    });
  }

  public static void setAdvancementRendered(Identifier advancementId, boolean rendered) {
    RENDER_ADVANCEMENT_MAP.replace(advancementId, rendered);
  }
}
