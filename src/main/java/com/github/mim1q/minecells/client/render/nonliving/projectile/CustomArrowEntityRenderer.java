package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.HashMap;

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
      var maxAge = entity.getArrowType().getMaxAge();
      var age = entity.age + tickDelta;

      if (age >= maxAge - 1) {
        var scale = 1.0f - (age - (maxAge - 1));
        matrices.scale(scale, scale, scale);
      }

      if (entity.getArrowType() == CustomArrowType.FIREBRANDS) {
        var time = (entity.age + tickDelta) * -30f;
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(time));
      }

      matrices.translate(-0.5, -0.5, 0.0);
      RenderUtils.renderBakedModel(model, entity.getWorld().getRandom(), light, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()));
    }
    matrices.pop();
  }

  @Override
  public Identifier getTexture(CustomArrowEntity entity) {
    return null;
  }
}
