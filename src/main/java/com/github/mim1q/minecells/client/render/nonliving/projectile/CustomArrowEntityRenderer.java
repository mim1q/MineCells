package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;

public class CustomArrowEntityRenderer extends EntityRenderer<CustomArrowEntity> {
  private final HashMap<CustomArrowType, BakedModel> MODELS = new HashMap<>();

  public CustomArrowEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
    CustomArrowType.getAllNames().forEach(name -> {
      var model = ctx.getModelManager().getModel(MineCells.createId("arrow/" + name));
      MODELS.put(CustomArrowType.get(name), model);
    });
  }

  @Override
  public void render(CustomArrowEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    var model = MODELS.get(entity.getArrowType());

    matrices.push();
    {
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f + entity.getYaw()));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));
      matrices.translate(-0.5, -0.5, 0.0);
      renderBakedArrowModel(model, entity.getWorld().getRandom(), light, matrices, vertexConsumers);
    }
    matrices.pop();
  }

  @Override
  public Identifier getTexture(CustomArrowEntity entity) {
    return null;
  }

  private static void renderBakedArrowModel(BakedModel model, Random random, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
    var buffer = vertexConsumers.getBuffer(RenderLayer.getCutout());

    for (var direction : Direction.values()) {
      var quads = model.getQuads(null, direction, random);
      renderBakedQuads(quads, matrices, buffer, light);
    }

    var noDirectionQuads = model.getQuads(null, null, random);
    renderBakedQuads(noDirectionQuads, matrices, buffer, light);
  }

  private static void renderBakedQuads(List<BakedQuad> quads, MatrixStack matrices, VertexConsumer buffer, int light) {
    for (var quad : quads) {
      buffer.quad(
        matrices.peek(),
        quad,
        new float[]{1.0F, 1.0F, 1.0F, 1.0F},
        1f, 1f, 1f,
        new int[]{light, light, light, light},
        OverlayTexture.DEFAULT_UV,
        false
      );
    }
  }
}
