package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.MutatedBatEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class MutatedBatEntityModel extends EntityModel<MutatedBatEntity> {

  private final ModelPart root;
  private final ModelPart rightWing;
  private final ModelPart leftWing;
  private final ModelPart tailFront;
  private final ModelPart tailBack;

  public MutatedBatEntityModel(ModelPart root) {
    this.root = root.getChild("root");
    this.rightWing = this.root.getChild("right_wing");
    this.leftWing = this.root.getChild("left_wing");
    this.tailFront = this.root.getChild("tail_front");
    this.tailBack = this.tailFront.getChild("tail_back");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    ModelPartData dRoot = modelPartData.addChild("root",
      ModelPartBuilder.create()
        .uv(22, 16)
        .cuboid(-2.5F, -5.0F, -5.0F, 5, 5, 5),
      ModelTransform.pivot(0.0F, 24.0F, 0.0F)
    );

    dRoot.addChild("head",
      ModelPartBuilder.create()
        .uv(12, 27)
        .cuboid(-1.5F, -1.0F, -2.0F, 3, 4, 2)
        .uv(22, 26)
        .cuboid(-4.5F, -1.0F, -1.0F, 9, 8, 0, new Dilation(0.01F)),
      ModelTransform.pivot(0.0F, -3.0F, -5.0F)
    );

    dRoot.addChild("left_wing",
      ModelPartBuilder.create()
        .uv(0, 8)
        .cuboid(0.0F, 0.0F, -4.0F, 12, 0, 8, new Dilation(0.01F)),
      ModelTransform.pivot(1.5F, -5.0F, -2.5F)
    );

    dRoot.addChild("right_wing",
      ModelPartBuilder.create()
        .uv(0, 0)
        .cuboid(-12.0F, 0.0F, -4.0F, 12, 0, 8, new Dilation(0.01F)),
      ModelTransform.pivot(-1.5F, -5.0F, -2.5F)
    );

    ModelPartData dTailFront = dRoot.addChild("tail_front",
      ModelPartBuilder.create()
        .uv(0, 16)
        .cuboid(-2.0F, -2.0F, 0.0F, 4, 4, 7),
      ModelTransform.pivot(0.0F, -2.5F, 0.0F)
    );

    dTailFront.addChild("tail_back",
      ModelPartBuilder.create()
        .uv(0, 27)
        .cuboid(-2.0F, -2.0F, 0.0F, 3, 3, 3),
      ModelTransform.pivot(0.0F, 0.5F, 7.0F)
    );

    return TexturedModelData.of(modelData, 64, 64);
  }

  @Override
  public void setAngles(MutatedBatEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    this.root.pitch = headPitch * RADIANS_PER_DEGREE;
    this.root.yaw = headYaw * RADIANS_PER_DEGREE;

    this.root.pivotY = 23.0F;
    this.root.pivotY += MathHelper.sin(animationProgress * 0.5F - 1.5F) * 2.0F;

    this.leftWing.roll = -MathHelper.PI * 0.25F - (float) Math.sin(animationProgress * 0.5F) * 0.5F;
    this.rightWing.roll = -this.leftWing.roll;

    this.tailFront.pitch = (-15.0F + (float) Math.sin(animationProgress * 0.5F - 1.0F) * 10.0F) * RADIANS_PER_DEGREE;
    this.tailBack.pitch = this.tailFront.pitch;

  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }
}
