package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Math;

import static com.github.mim1q.minecells.util.MathUtils.radians;
import static com.github.mim1q.minecells.util.animation.AnimationUtils.wobble;
import static net.minecraft.util.math.MathHelper.HALF_PI;
import static net.minecraft.util.math.MathHelper.PI;
import static org.joml.Math.*;

public class ConciergeEntityModel extends EntityModel<ConciergeEntity> {
  private final ModelPart root;
  private final ModelPart torsoLower;
  private final ModelPart torsoUpper;
  private final ModelPart neck;
  private final ModelPart head;
  private final ModelPart rightArm;
  private final ModelPart leftArm;
  private final ModelPart rightLeg;
  private final ModelPart leftLeg;

  public ConciergeEntityModel(ModelPart root) {
    this.root = root;
    this.torsoLower = root.getChild("torso_lower");
    this.torsoUpper = torsoLower.getChild("torso_upper");
    this.neck = torsoUpper.getChild("neck");
    this.head = neck.getChild("head");
    this.rightArm = torsoUpper.getChild("right_arm");
    this.leftArm = torsoUpper.getChild("left_arm");
    this.rightLeg = root.getChild("right_leg");
    this.leftLeg = root.getChild("left_leg");
  }

  @Override
  public void setAngles(ConciergeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    // walking
    rightArm.pitch = radians(-20F) - wobble(limbAngle, -0.5F, 90F * limbDistance, -20F);
    rightArm.yaw = radians(25F);
    leftArm.pitch = radians(-20F) - wobble(limbAngle, 0.5F, 90F * limbDistance, -20F);
    leftArm.yaw = radians(-15F);
    rightLeg.pitch = wobble(limbAngle, -0.5F, 60F * limbDistance);
    rightLeg.pivotY = 2.0F - max(0, sin(limbAngle * 0.5F + radians(80F))) * 4 * limbDistance;
    leftLeg.pitch = wobble(limbAngle, 0.5F, 60F * limbDistance);
    leftLeg.pivotY = 2.0F - max(0, sin(limbAngle * 0.5F - radians(100F))) * 4 * limbDistance;
    torsoLower.pitch = radians(-5F) + wobble(limbAngle, 1F, 10F * limbDistance);
    torsoLower.yaw = wobble(limbAngle, 0.5F, 20F * limbDistance);
    torsoLower.pivotY = 3.0F + abs(sin(limbAngle * 0.5F + radians(180F))) * 3 * limbDistance;
    torsoUpper.pitch = radians(35F) + wobble(limbAngle, 1F, 10F * limbDistance);
    torsoUpper.yaw = wobble(limbAngle, 0.5F, 20F * limbDistance, 15F);
    torsoUpper.roll = wobble(limbAngle, 0.5F, 10F * limbDistance, 30F);

    neck.pitch = -0.8F * (torsoLower.pitch + torsoUpper.pitch);
    neck.yaw = -0.9F * torsoUpper.yaw;
    head.yaw = radians(headYaw);
    head.pitch = radians(headPitch);
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }

  public static TexturedModelData getTexturedModelData() {
    var modelData = new ModelData();
    var modelPartData = modelData.getRoot();modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(24, 65).cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 24.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));
    var torsoLower = modelPartData.addChild("torso_lower", ModelPartBuilder.create().uv(0, 42).cuboid(-7.0F, -18.0F, -4.0F, 14.0F, 10.0F, 8.0F, new Dilation(0.0F))
      .uv(0, 26).cuboid(-8.0F, -8.0F, -5.0F, 16.0F, 6.0F, 10.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));
    var torsoUpper = torsoLower.addChild("torso_upper", ModelPartBuilder.create().uv(0, 0).cuboid(-11.0F, -14.0F, -9.5F, 22.0F, 14.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -16.0F, 2.5F));
    torsoUpper.addChild("cube_r1", ModelPartBuilder.create().uv(67, 36).cuboid(-2.0F, -4.0F, 0.0F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -10.0F, 2.5F, 0.5236F, -0.2618F, 0.0F));
    torsoUpper.addChild("cube_r2", ModelPartBuilder.create().uv(67, 36).mirrored().cuboid(0.0F, -4.0F, 0.0F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(4.0F, -10.0F, 2.5F, 0.5236F, 0.2618F, 0.0F));
    var neck = torsoUpper.addChild("neck", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -11.0F, -8.5F));
    neck.addChild("head", ModelPartBuilder.create().uv(59, 17).cuboid(-3.5F, -8.0F, -8.0F, 7.0F, 10.0F, 9.0F, new Dilation(0.0F))
      .uv(68, 0).cuboid(-2.5F, -14.0F, -8.0F, 5.0F, 6.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));
    torsoUpper.addChild("right_arm", ModelPartBuilder.create().uv(66, 63).cuboid(-6.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
      .uv(0, 60).cuboid(-5.0F, 2.0F, -3.0F, 6.0F, 28.0F, 6.0F, new Dilation(0.0F))
      .uv(44, 42).cuboid(-6.0F, 13.0F, -3.5F, 8.0F, 22.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-11.0F, -11.0F, -2.5F));
    torsoUpper.addChild("left_arm", ModelPartBuilder.create().uv(66, 63).mirrored().cuboid(-2.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)).mirrored(false)
      .uv(0, 60).mirrored().cuboid(-1.0F, 2.0F, -3.0F, 6.0F, 28.0F, 6.0F, new Dilation(0.0F)).mirrored(false)
      .uv(44, 42).mirrored().cuboid(-2.0F, 13.0F, -3.5F, 8.0F, 22.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(11.0F, -11.0F, -2.5F));
    modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(24, 65).mirrored().cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 24.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
    return TexturedModelData.of(modelData, 128, 128);
  }
}
