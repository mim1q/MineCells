package com.github.mim1q.minecells.util.animation;

import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

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
    return (float) Math.sin(MathUtils.radians(offset) + progress * speed) * MathUtils.radians(scale);
  }

  public static float wobble(float progress, float speed, float scale) {
    return wobble(progress, speed, scale, 0);
  }

  public static float wobble(float progress, float speed) {
    return wobble(progress, speed, 1F);
  }
}
