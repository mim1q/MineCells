package com.github.mim1q.minecells.util;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

public class AnimationHelper {
    public static void bipedZombieWalk(float limbAngle, float limbDistance, ModelPart root, ModelPart rightLeg, ModelPart leftLeg, ModelPart rightArm, ModelPart leftArm, ModelPart lowerTorso, ModelPart upperTorso) {
        float rightLegPitch = (MathHelper.sin(limbAngle / 2.0F) * limbDistance);

        rightLeg.pitch = rightLegPitch;
        leftLeg.pitch = -rightLegPitch;
        leftArm.pitch = -rightLegPitch - 20.0F * MathHelper.RADIANS_PER_DEGREE;
        rightArm.pitch = rightLegPitch - 20.0F * MathHelper.RADIANS_PER_DEGREE;

        rightLeg.pivotY = 2.0F + (MathHelper.sin(limbAngle + MathHelper.HALF_PI / 2.0F) * limbDistance);
        leftLeg.pivotY =  2.0F + (MathHelper.sin(limbAngle - MathHelper.HALF_PI / 2.0F) * limbDistance);
        root.pivotY = 24.0F - MathHelper.abs((MathHelper.sin((limbAngle + MathHelper.PI) / 2.0F)) * limbDistance) * 2.0F;

        upperTorso.roll = (MathHelper.sin((limbAngle + MathHelper.PI * 0.75F) / 2.0F) * limbDistance * 10.0F) * MathHelper.RADIANS_PER_DEGREE;
        upperTorso.pitch = (20.0F + MathHelper.sin((limbAngle + MathHelper.PI) / 2.0F) * limbDistance * 5.0F) * MathHelper.RADIANS_PER_DEGREE;
        lowerTorso.pitch = (20.0F + MathHelper.sin((limbAngle + MathHelper.PI) / 2.0F) * limbDistance * 5.0F) * MathHelper.RADIANS_PER_DEGREE;
    }
}
