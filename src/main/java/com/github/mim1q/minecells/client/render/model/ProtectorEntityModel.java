package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.ProtectorEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class ProtectorEntityModel extends EntityModel<ProtectorEntity> {

  private final ModelPart root;

  public ProtectorEntityModel(ModelPart root) {
    super(RenderLayer::getEntityCutout);
    this.root = root.getChild("root");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    ModelPartData dRoot = modelPartData.addChild("root",
      ModelPartBuilder.create()
        .uv(28, 31)
        .cuboid(-1.5F, -10.0F, -1.5F, 3, 10, 3),
      ModelTransform.pivot(0.0F, 24.0F, 0.0F));

    ModelPartData dBody = dRoot.addChild("torso",
      ModelPartBuilder.create()
        .uv(1, 0)
        .cuboid(-3.5F, -12.0F, -3.5F, 7, 12, 7)
        .uv(28, 55)
        .cuboid(-4.0F, -3.0F, -4.0F, 8, 1, 8)
        .uv(0, 49)
        .cuboid(2.0F, -13.0F, -4.0F, 3, 2, 8)
        .uv(0, 39)
        .cuboid(-5.0F, -13.0F, -4.0F, 3, 2, 8),
      ModelTransform.pivot(0.0F, -10.0F, 0.0F));

    ModelPartData dHead = dBody.addChild("head",
      ModelPartBuilder.create()
        .uv(1, 20)
        .cuboid(-3.0F, -7.0F, -3.0F, 6, 7, 6)
        .uv(38, 27)
        .cuboid(-3.0F, -10.0F, -3.0F, 6, 3, 0)
        .cuboid(-3.0F, -10.0F, 3.0F, 6, 3, 0)
        .uv(-6, 19)
        .cuboid(-0.5F, -9.9F, -3.0F, 1, 0, 6),
      ModelTransform.pivot(0.0F, -12.0F, 0.0F));

    dHead.addChild("left_slope",
      ModelPartBuilder.create()
        .uv(0, 28)
        .cuboid(0.0F, -4.0F, -3.0F, 0, 4, 6),
      ModelTransform.of(3.0F, -7.0F, 0.0F, 0.0F, 0.0F, -45.0F * RADIANS_PER_DEGREE));

    dHead.addChild("right_slope",
      ModelPartBuilder.create()
        .uv(0, 28)
        .cuboid(0.0F, -4.0F, -3.0F, 0, 4, 6),
      ModelTransform.of(-3.0F, -7.0F, 0.0F, 0.0F, 0.0F, 45.0F * RADIANS_PER_DEGREE));

    ModelPartData dLeftArm = dBody.addChild("left_arm",
      ModelPartBuilder.create()
        .uv(32, 7)
        .cuboid(0.0F, -1.5F, -1.5F, 8, 3, 3),
      ModelTransform.pivot(3.0F, -10.5F, 0.0F));

    dLeftArm.addChild("shield",
      ModelPartBuilder.create()
        .uv(30, 17)
        .cuboid(-4.0F, -4.0F, -2.0F, 8, 8, 2),
      ModelTransform.of(6.0F, 0.0F, -1.0F, 0.0F, -25.0F * RADIANS_PER_DEGREE, 0.0F));

    dBody.addChild("right_arm",
      ModelPartBuilder.create()
        .uv(25, 0)
        .cuboid(-8.0F, -1.5F, -1.5F, 8, 3, 3),
      ModelTransform.pivot(-3.0F, -10.5F, 0.0F));

    return TexturedModelData.of(modelData, 64, 64);
  }

  @Override
  public void setAngles(ProtectorEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    float angle = animationProgress * 10.0F;
    float x = (float) Math.sin(angle * RADIANS_PER_DEGREE);
    float y = (float) Math.cos(angle * RADIANS_PER_DEGREE);
    this.root.pitch = limbDistance * x * 0.3F;
    this.root.roll = limbDistance * y * 0.3F;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }


}
