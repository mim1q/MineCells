package com.github.mim1q.minecells.util.animation;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;
import org.joml.Math;

import static com.github.mim1q.minecells.util.MathUtils.radians;
import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class AnimationUtils {
  public static void bipedWalk(float limbAngle, float limbDistance, ModelPart root, ModelPart rightLeg, ModelPart leftLeg, ModelPart rightArm, ModelPart leftArm, ModelPart lowerTorso, ModelPart upperTorso) {
    float rightLegPitch = MathHelper.sin(limbAngle * 0.5F) * limbDistance;

    rightLeg.pitch = rightLegPitch;
    leftLeg.pitch = -rightLegPitch;
    leftArm.pitch = -rightLegPitch;
    rightArm.pitch = rightLegPitch;

    root.pivotY = 24.0F - MathHelper.abs(MathHelper.sin((limbAngle + MathHelper.PI) * 0.5F)) * limbDistance;

    float torso = MathHelper.sin(limbAngle + MathHelper.PI) * limbDistance * RADIANS_PER_DEGREE;
    if (upperTorso != null) {
      upperTorso.pitch = torso;
    }
    if (lowerTorso != null) {
      lowerTorso.pitch = torso;
    }
  }

  public static void rotateHead(float headYaw, float headPitch, ModelPart head) {
    head.yaw = headYaw * RADIANS_PER_DEGREE;
    head.pitch = headPitch * RADIANS_PER_DEGREE;
  }

  public static void lerpModelPartRotation(ModelPart part, float pitch, float yaw, float roll, float delta) {
    part.pitch = MathHelper.lerp(delta, part.pitch, pitch * RADIANS_PER_DEGREE);
    part.yaw = MathHelper.lerp(delta, part.yaw, yaw * RADIANS_PER_DEGREE);
    part.roll = MathHelper.lerp(delta, part.roll, roll * RADIANS_PER_DEGREE);
  }

  public static float wobble(float progress, float speed, float scale, float offset) {
    return Math.sin(radians(offset) + progress * speed) * radians(scale);
  }

  public static float wobble(float progress, float speed, float scale) {
    return wobble(progress, speed, scale, 0);
  }

  public static float wobble(float progress, float speed) {
    return wobble(progress, speed, 1F);
  }

  public static void lerpAngles(ModelPart bone, Float pitchDeg, Float yawDeg, Float rollDeg, float delta) {
    if (pitchDeg != null) bone.pitch = MathHelper.lerp(delta, bone.pitch, radians(pitchDeg));
    if (yawDeg != null) bone.yaw = MathHelper.lerp(delta, bone.yaw, radians(yawDeg));
    if (rollDeg != null) bone.roll = MathHelper.lerp(delta, bone.roll, radians(rollDeg));
  }

  public static void lerpAngles(ModelPart bone, float pitchDeg, float yawDeg, float rollDeg, float delta) {
    bone.pitch = MathHelper.lerp(delta, bone.pitch, radians(pitchDeg));
    bone.yaw = MathHelper.lerp(delta, bone.yaw, radians(yawDeg));
    bone.roll = MathHelper.lerp(delta, bone.roll, radians(rollDeg));
  }
}
