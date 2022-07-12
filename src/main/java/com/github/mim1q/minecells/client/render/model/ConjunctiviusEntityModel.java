package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ConjunctiviusEntityModel extends EntityModel<ConjunctiviusEntity> {

  private final ModelPart main;

  public ConjunctiviusEntityModel(ModelPart root) {
    this.main = root.getChild("main");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    ModelPartData dMain = modelPartData.addChild("main",
      ModelPartBuilder.create(),
      ModelTransform.pivot(0.0F, 24.0F, 0.0F)
    );

    return TexturedModelData.of(modelData, 256, 256);
  }

  @Override
  public void setAngles(ConjunctiviusEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

  }
}
