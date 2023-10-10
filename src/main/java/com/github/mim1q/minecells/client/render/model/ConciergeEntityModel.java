package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import static com.github.mim1q.minecells.util.MathUtils.radians;
import static com.github.mim1q.minecells.util.animation.AnimationUtils.*;
import static net.minecraft.util.math.MathHelper.EPSILON;
import static org.joml.Math.*;

public class ConciergeEntityModel extends EntityModel<ConciergeEntity> {
  private boolean entityAlive = true;

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
    entityAlive = entity.isAlive();
    root.traverse().forEach(ModelPart::resetTransform);

    var idleProgress = 1 - limbDistance;
    animateIdle(animationProgress, idleProgress);
    if (limbDistance > EPSILON) animateWalk(limbAngle, limbDistance);
    animateHead(headYaw, headPitch);

    animate(entity.leapChargeAnimation,   this::animateLeapCharge,       animationProgress);
    animate(entity.leapReleaseAnimation,  this::animateLeapRelease,      animationProgress);
    animate(entity.waveChargeAnimation,   this::animateShockwaveCharge,  animationProgress);
    animate(entity.waveReleaseAnimation,  this::animateShockwaveRelease, animationProgress);
    animate(entity.punchChargeAnimation,  this::animatePunchCharge,      animationProgress);
    animate(entity.punchReleaseAnimation, this::animatePunchRelease,     animationProgress);
    animate(entity.deathStartAnimation,   this::animateDeathStart,       animationProgress);
    animate(entity.deathFallAnimation,    this::animateDeathFall,        animationProgress);
  }

  private void animateIdle(float animationProgress, float delta) {
    torsoUpper.pitch = radians(35F) + wobble(animationProgress, 0.1F, 5F * delta);
    torsoLower.pitch = radians(-5F) + wobble(animationProgress, 0.1F, 3F * delta, 15F);
    rightArm.pitch = radians(-30F) + wobble(animationProgress, 0.1F, -10F * delta, 30F);
    leftArm.pitch = rightArm.pitch;
    rightArm.yaw = radians(15F);
    leftArm.yaw = radians(-15F);
  }

  private void animateWalk(float limbAngle, float limbDistance) {
    rightArm.pitch -= wobble(limbAngle, -0.5F, 90F * limbDistance, -20F);
    leftArm.pitch -= wobble(limbAngle, 0.5F, 90F * limbDistance, -20F);
    rightLeg.pitch = wobble(limbAngle, -0.5F, 60F * limbDistance);
    rightLeg.pivotY -= max(0, sin(limbAngle * 0.5F + radians(80F))) * 4 * limbDistance;
    leftLeg.pitch = wobble(limbAngle, 0.5F, 60F * limbDistance);
    leftLeg.pivotY -= max(0, sin(limbAngle * 0.5F - radians(100F))) * 4 * limbDistance;
    torsoLower.pitch += wobble(limbAngle, 1F, 10F * limbDistance);
    torsoLower.yaw = wobble(limbAngle, 0.5F, 20F * limbDistance);
    torsoLower.pivotY += abs(sin(limbAngle * 0.5F + radians(180F))) * 3 * limbDistance;
    torsoUpper.pitch += wobble(limbAngle, 1F, 10F * limbDistance);
    torsoUpper.yaw = wobble(limbAngle, 0.5F, 20F * limbDistance, 15F);
    torsoUpper.roll = wobble(limbAngle, 0.5F, 10F * limbDistance, 30F);
  }

  private void animateHead(float headYaw, float headPitch) {
    neck.pitch = -0.8F * (torsoLower.pitch + torsoUpper.pitch);
    neck.yaw = -0.9F * torsoUpper.yaw;
    head.yaw = radians(headYaw);
    head.pitch = radians(headPitch);
  }

  private void animateLeapCharge(float delta) {
    lerpAngles(torsoLower, 30, 0, 20, delta);
    torsoLower.pivotY += 7 * delta;
    lerpAngles(torsoUpper, 30, 0, -10, delta);
    lerpAngles(leftArm, -60, 0, -30, delta);
    lerpAngles(rightArm, -30, 0, 20, delta);
    rightArm.pivotY -= 2 * delta;
    lerpAngles(rightLeg, 15, 0, 0, delta);
    rightLeg.pivotZ -= 10 * delta;
    lerpAngles(leftLeg, 60, 20, 10, delta);
    leftLeg.pivotY += 8 * delta;
    leftLeg.pivotZ -= 10 * delta;
    head.pitch += radians(20) * delta;
  }

  private void animateLeapRelease(float delta) {
    var leapReleaseLimbs = 1 - delta * 0.5;

    torsoLower.pitch += radians(5) * delta;
    torsoUpper.pitch -= radians(5) * delta;
    leftArm.roll -= radians(25) * delta;
    rightArm.roll += radians(25) * delta;
    head.pitch += radians(40) * delta;
    rightLeg.pitch *= leapReleaseLimbs;
    leftLeg.pitch *= leapReleaseLimbs;
    leftArm.pitch *= leapReleaseLimbs;
    rightArm.pitch *= leapReleaseLimbs;
  }

  private void animateShockwaveCharge(float delta) {
    lerpAngles(torsoLower, -10, 30, -25, delta);
    torsoLower.pivotY += 2 * delta;
    lerpAngles(torsoUpper, -15, 10, -25, delta);
    torsoUpper.pivotY += 3 * delta;
    lerpAngles(rightArm, 0, 20, 60, delta);
    lerpAngles(leftArm, 15, 0, 0, delta);
    leftLeg.pivotZ -= 3 * delta;
    lerpAngles(neck, 0, -45, 30, delta);
  }

  private void animateShockwaveRelease(float delta) {
    lerpAngles(torsoLower, 0, 15, 0, delta);
    lerpAngles(torsoUpper, 15, -20, 0, delta);
    lerpAngles(rightArm, -90, -30, 60, delta);
    lerpAngles(leftArm, 40, 0, 0, delta);
  }

  private void animatePunchCharge(float delta) {
    lerpAngles(torsoLower, 0, 15, 0, delta);
    lerpAngles(torsoUpper, -10, 10, 0, delta);
    lerpAngles(rightArm, -45, 0, 30, delta);
    rightArm.pivotY -= 4 * delta;
    rightArm.pivotZ += 4 * delta;
    rightLeg.pivotZ += 2 * delta;
    leftLeg.pivotZ -= 2 * delta;
    neck.yaw -= radians(10F) * delta;
    neck.pitch += radians(15F) * delta;
  }

  private void animatePunchRelease(float delta) {
    lerpAngles(torsoLower, 20, -10, 0, delta);
    torsoLower.pivotY += 3 * delta;
    lerpAngles(torsoUpper, 15, -10, 0, delta);
    lerpAngles(rightArm, -80, 0, -10, delta);
    rightArm.pivotY += 2 * delta;
    rightArm.pivotZ -= 4 * delta;
    lerpAngles(leftArm, 20, 0, -20, delta);
    rightLeg.pivotZ -= 2 * delta;
    lerpAngles(leftLeg, 25, 0, 0, delta);
    leftLeg.pivotY += 2 * delta;
    leftLeg.pivotZ += 2 * delta;
  }

  private void animateDeathStart(float delta) {
    var bounceDelta = MathUtils.easeOutBounce(0, 1, delta);
    var spedUpDelta = MathUtils.easeInOutQuad(0, 1, min(1, delta * 2));
    lerpAngles(torsoLower, -10, 0, 0, delta);
    torsoLower.pivotY += 17 * bounceDelta;
    lerpAngles(torsoUpper, 40, 0, 0, bounceDelta);
    lerpAngles(leftArm, -50, 0, -15, delta);
    lerpAngles(rightArm, -50, 0, 15, delta);
    lerpAngles(leftLeg, 85, 15, 0, spedUpDelta);
    leftLeg.pivotY += 17 * bounceDelta;
    leftLeg.pivotZ -= 4 * delta;
    lerpAngles(rightLeg, 85, -15, 0, spedUpDelta);
    rightLeg.pivotY += 17 * bounceDelta;
    rightLeg.pivotZ -= 4 * delta;
  }

  private void animateDeathFall(float delta) {
    var bounceDelta = MathUtils.easeOutBounce(0, 1, delta);
    var spedUpDelta = MathUtils.easeInOutQuad(0, 1, min(1, delta * 2.5F));
    lerpAngles(torsoLower, 80, 0, 0, bounceDelta);
    torsoLower.pivotZ -= 2 * delta;
    lerpAngles(torsoUpper, 10, 0, 0, bounceDelta);
    lerpAngles(leftArm, -5, 0, -40, spedUpDelta);
    lerpAngles(rightArm, -5, 0, 40, spedUpDelta);
    lerpAngles(head, -10, 110, 10, bounceDelta);
    lerpAngles(neck, 0, 0, 0, delta);
    head.pivotX += 2 * spedUpDelta;
    head.pivotY -= 5 * spedUpDelta;
    head.pivotZ += 1 * spedUpDelta;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    var overlayUv = entityAlive ? overlay : OverlayTexture.DEFAULT_UV;
    this.root.render(matrices, vertices, light, overlayUv, red, green, blue, alpha);
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
