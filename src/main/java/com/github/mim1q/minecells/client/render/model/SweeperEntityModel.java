package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.SweeperEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import static com.github.mim1q.minecells.util.MathUtils.lerp;
import static com.github.mim1q.minecells.util.MathUtils.radians;
import static com.github.mim1q.minecells.util.animation.AnimationUtils.wobble;

public class SweeperEntityModel extends EntityModel<SweeperEntity> {
  private final ModelPart root;
  private final ModelPart torsoWrapper;
  private final ModelPart lowerTorso;
  private final ModelPart upperTorso;
  private final ModelPart neck;
  private final ModelPart head;
  private final ModelPart leftArm;
  private final ModelPart rightArm;
  private final ModelPart leftLeg;
  private final ModelPart rightLeg;

  public SweeperEntityModel(ModelPart root) {
    this.root = root;
    this.torsoWrapper = root.getChild("torso_wrapper");
    this.lowerTorso = torsoWrapper.getChild("lower_torso");
    this.upperTorso = lowerTorso.getChild("upper_torso");
    this.neck = upperTorso.getChild("neck");
    this.head = neck.getChild("head");
    this.leftArm = upperTorso.getChild("left_arm");
    this.rightArm = upperTorso.getChild("right_arm");
    this.leftLeg = torsoWrapper.getChild("left_leg");
    this.rightLeg = root.getChild("right_leg");
  }

  @Override
  public void setAngles(SweeperEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    this.lowerTorso.pitch = radians(10F) + wobble(animationProgress, 0.1F, 5F);
    this.upperTorso.pitch = radians(10F) + wobble(animationProgress, 0.1F, 5F, -15F);
    this.lowerTorso.roll = radians(-10F + limbDistance * 35F);
    this.upperTorso.roll = radians(5F + limbDistance * 25F);

    this.neck.pitch = radians(headPitch) - upperTorso.pitch - lowerTorso.pitch;
    this.head.yaw = radians(headYaw);

    this.leftArm.pitch = (float) Math.sin(limbAngle * 0.5F) * limbDistance - wobble(animationProgress, 0.1F, 15F, -30F) - radians(20F);
    this.rightArm.pitch = radians(35F) - lowerTorso.pitch - upperTorso.pitch * 0.75F;
    this.rightArm.roll = radians(15F - limbDistance * 50F);
    this.rightArm.yaw = radians(limbDistance * -15F);
    this.rightLeg.pitch = (float) Math.sin(limbAngle * 0.5) * limbDistance;
    this.leftArm.roll = radians(limbDistance * -45F);
    this.leftLeg.pitch = -this.rightLeg.pitch;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();
    ModelPartData torso_wrapper = modelPartData.addChild("torso_wrapper", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, 14.0F, 0.0F));
    ModelPartData lower_torso = torso_wrapper.addChild("lower_torso", ModelPartBuilder.create().uv(0, 17).cuboid(-6.0F, -10.0F, -3.0F, 12.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, 0.0F, 0.0F));
    ModelPartData upper_torso = lower_torso.addChild("upper_torso", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -8.0F, -7.5F, 12.0F, 8.0F, 9.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, -10.0F, 2.5F));
    ModelPartData neck = upper_torso.addChild("neck", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -2.0F, -7.5F));
    neck.addChild("head", ModelPartBuilder.create().uv(12, 51).cuboid(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
    upper_torso.addChild("left_arm", ModelPartBuilder.create().uv(0, 48).cuboid(0.0F, -1.5F, -1.5F, 3.0F, 24.0F, 3.0F, new Dilation(0.0F))
      .uv(42, 0).cuboid(-0.5F, -2.5F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, -4.5F, -2.5F));
    ModelPartData right_arm = upper_torso.addChild("right_arm", ModelPartBuilder.create().uv(31, 28).cuboid(-5.0F, -2.5F, -2.5F, 5.0F, 24.0F, 5.0F, new Dilation(0.0F))
      .uv(0, 33).cuboid(-6.9F, -3.5F, -3.5F, 7.0F, 8.0F, 7.0F, new Dilation(0.0F))
      .uv(36, 11).cuboid(-5.5F, 17.0F, -3.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, -3.5F, -2.5F));
    right_arm.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -4.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, -2.5F, 0.0F, 0.0F, 0.0F, -0.7854F));
    right_arm.addChild("cube_r2", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -5.0F, -2.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -3.5F, -0.5F, 0.3491F, 0.0F, -0.3491F));
    right_arm.addChild("cube_r3", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -3.5F, 0.5F, -0.3491F, 0.0F, -0.3491F));
    torso_wrapper.addChild("left_leg", ModelPartBuilder.create().uv(51, 41).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(7.5F, 0.0F, -0.5F));
    modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(51, 28).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.5F, 14.0F, -0.5F));
    return TexturedModelData.of(modelData, 128, 128);
  }
}
