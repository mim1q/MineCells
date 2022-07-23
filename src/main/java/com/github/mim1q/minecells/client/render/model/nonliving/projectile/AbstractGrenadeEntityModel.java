package com.github.mim1q.minecells.client.render.model.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.GrenadeEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractGrenadeEntityModel<E extends GrenadeEntity> extends EntityModel<E> {

  private final ModelPart body;

  public AbstractGrenadeEntityModel(ModelPart root) {
    this.body = root.getChild("body");
  }

  public static TexturedModelData getTexturedModelData() {
    return getTexturedModelData(8.0F);
  }

  public static TexturedModelData getTexturedModelData(float size) {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();
    modelPartData.addChild("body",
      ModelPartBuilder.create()
        .uv(0, 0)
        .cuboid(-size / 2.0F, 0.0F, -size / 2.0F,
          size, size, size),
      ModelTransform.NONE
    );
    return TexturedModelData.of(modelData, (int) size * 4, (int) size * 2);
  }

  @Override
  public void setAngles(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    ImmutableList.of(this.body).forEach((modelRenderer) -> modelRenderer.render(matrices, vertices, light, overlay, red, green, blue, alpha));
  }
}
