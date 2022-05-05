package com.github.mim1q.minecells.util;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

public class AnimationHelper {
    public static void bipedWalk(float limbAngle, float limbDistance, ModelPart root, ModelPart rightLeg, ModelPart leftLeg, ModelPart rightArm, ModelPart leftArm, ModelPart lowerTorso, ModelPart upperTorso) {
        float rightLegPitch = MathHelper.sin(limbAngle * 0.5F) * limbDistance;

        rightLeg.pitch = rightLegPitch;
        leftLeg.pitch = -rightLegPitch;
        leftArm.pitch = -rightLegPitch;
        rightArm.pitch = rightLegPitch;

        root.pivotY = 24.0F - MathHelper.abs(MathHelper.sin((limbAngle + MathHelper.PI) * 0.5F)) * limbDistance;

        upperTorso.roll = MathHelper.sin((limbAngle + MathHelper.PI) * 0.5F) * limbDistance * MathHelper.RADIANS_PER_DEGREE;
        upperTorso.pitch = MathHelper.sin(limbAngle + MathHelper.PI) * limbDistance * MathHelper.RADIANS_PER_DEGREE;
        lowerTorso.pitch = upperTorso.pitch;
    }

    public static void rotateHead(float headYaw, float headPitch, ModelPart head) {
        head.yaw = headYaw * MathHelper.RADIANS_PER_DEGREE;
        head.pitch = headPitch * MathHelper.RADIANS_PER_DEGREE;
    }
}
