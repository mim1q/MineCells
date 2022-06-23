package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.RunnerEntity;
import com.github.mim1q.minecells.util.animation.AnimationUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class RunnerEntityModel extends EntityModel<RunnerEntity> {

    private final ModelPart root;
    private final ModelPart lowerTorso;
    private final ModelPart upperTorso;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart neck;
    private final ModelPart head;

    public RunnerEntityModel(ModelPart part) {
        System.out.println("RunnerEntityModel");

        this.root = part.getChild("root");
        this.lowerTorso = this.root.getChild("lower_torso");
        this.upperTorso = this.lowerTorso.getChild("upper_torso");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.leftArm = this.upperTorso.getChild("left_arm");
        this.rightArm = this.upperTorso.getChild("right_arm");
        this.neck = this.upperTorso.getChild("neck");
        this.head = this.neck.getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData dRoot = modelPartData.addChild("root",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F)
        );

        ModelPartData dLowerTorso = dRoot.addChild("lower_torso",
            ModelPartBuilder.create()
                .uv(32, 25)
                .cuboid(-3.5F, -4.0F, -1.5F, 7, 4, 3),
            ModelTransform.pivot(0.0F, -18.0F, 0.0F)
        );

        dRoot.addChild("left_leg",
            ModelPartBuilder.create()
                .uv(32, 32)
                .cuboid(-1.5F, 8.0F, -1.5F, 3, 10, 3)
                .uv(16, 25)
                .cuboid(-2.0F, 0.0F, -2.0F, 4, 10, 4),
            ModelTransform.pivot(2.0F, -18.0F, 0.0F)
        );

        dRoot.addChild("right_leg",
            ModelPartBuilder.create()
                .uv(32, 32).mirrored()
                .cuboid(-1.5F, 8.0F, -1.5F, 3, 10, 3)
                .uv(16, 25).mirrored()
                .cuboid(-2.0F, 0.0F, -2.0F, 4, 10, 4),
            ModelTransform.pivot(-2.0F, -18.0F, 0.0F)
        );

        ModelPartData dUpperTorso = dLowerTorso.addChild("upper_torso",
            ModelPartBuilder.create()
                .uv(0, 14)
                .cuboid(-4.0F, -5.0F, -2.5F, 8, 5, 5)
                .uv(21, 7)
                .cuboid(3.0F, -5.0F, -3.5F, 5, 2, 7)
                .uv(20, 18)
                .cuboid(2.5F, -6.0F, -3.0F, 7, 1, 6)
                .uv(21, 7).mirrored()
                .cuboid(-8.0F, -5.0F, -3.5F, 5, 2, 7)
                .uv(20, 18).mirrored()
                .cuboid(-9.5F, -6.0F, -3.0F, 7, 1, 6),
            ModelTransform.pivot(0.0F, -4.0F, 0.0F)
        );

        dUpperTorso.addChild("left_arm",
            ModelPartBuilder.create()
                .uv(10, 39)
                .cuboid(0.0F, -1.0F, -1.5F, 2, 8, 3)
                .uv(22, 0)
                .cuboid(-1.5F, 7.0F, -2.5F, 5, 2, 5)
                .uv(0, 38)
                .cuboid(-1.5F, 9.0F, 0.0F, 5, 12, 0, new Dilation(0.01F)),
            ModelTransform.pivot(4.0F, -3.5F, 0.0F)
        );

        dUpperTorso.addChild("right_arm",
            ModelPartBuilder.create()
                .uv(10, 39).mirrored()
                .cuboid(-2.0F, -1.0F, -1.5F, 2, 8, 3)
                .uv(22, 0).mirrored()
                .cuboid(-3.5F, 7.0F, -2.5F, 5, 2, 5)
                .uv(0, 38).mirrored()
                .cuboid(-3.5F, 9.0F, 0.0F, 5, 12, 0, new Dilation(0.01F)),
            ModelTransform.pivot(-4.0F, -3.5F, 0.0F)
        );

        ModelPartData dNeck = dUpperTorso.addChild("neck",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, -5.0F, -0.5F)
        );

        dNeck.addChild("head",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-2.5F, -8.0F, -3.0F, 5, 8, 6),
            ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(RunnerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        // Default / walking pose
        AnimationUtils.rotateHead(headYaw, headPitch, this.head);
        AnimationUtils.bipedWalk(limbAngle, limbDistance, this.root, this.rightLeg, this.leftLeg, this.rightArm, this.leftArm, this.lowerTorso, this.upperTorso);
        this.rightArm.roll = 35.0F * RADIANS_PER_DEGREE;
        this.leftArm.roll = -35.0F * RADIANS_PER_DEGREE;
        this.rightArm.pitch = 0.0F;
        this.leftArm.pitch = 0.0F;
        this.upperTorso.roll = 0.0F;
        this.upperTorso.yaw = 0.0F;
        this.lowerTorso.yaw = 0.0F;
        this.leftArm.yaw = 0.0F;
        this.rightArm.yaw = 0.0F;

        // Setup transitions of animation properties

        if (entity.isAttacking() && entity.getVelocity().length() > 0.1D) {
            entity.bendAngle.setupTransitionTo(30.0F, 10.0F);
        } else {
            entity.bendAngle.setupTransitionTo(0.0F, 10.0F);
        }

        if (entity.getDataTracker().get(RunnerEntity.TIMED_ATTACK_CHARGING)) {
            entity.swingChargeProgress.setupTransitionTo(1.0F, 10.0F);
        } else {
            entity.swingChargeProgress.setupTransitionTo(0.0F, 5.0F);
        }

        if (entity.getDataTracker().get(RunnerEntity.TIMED_ATTACK_RELEASING)) {
            entity.swingReleaseProgress.setupTransitionTo(1.0F, 3.0F);
        } else {
            entity.swingReleaseProgress.setupTransitionTo(0.0F, 10.0F);
        }

        entity.bendAngle.updateQuad(animationProgress);
        entity.swingChargeProgress.updateQuad(animationProgress);
        entity.swingReleaseProgress.updateQuad(animationProgress);

        // Running animation
        float angle = entity.bendAngle.getValue() * RADIANS_PER_DEGREE;
        this.leftArm.pitch += angle;
        this.rightArm.pitch += angle;
        this.lowerTorso.pitch += angle * 0.5F;
        this.upperTorso.pitch += angle * 0.5F;
        this.neck.pitch = -angle;

        // Melee attack charge animation
        float swingChargeProgress = entity.swingChargeProgress.getValue();
        AnimationUtils.lerpModelPartRotation(this.leftArm, 45.0F, 15.0F, -90.0F, swingChargeProgress);
        AnimationUtils.lerpModelPartRotation(this.rightArm, -45.0F, 35.0F, -45.0F, swingChargeProgress);
        AnimationUtils.lerpModelPartRotation(this.upperTorso, 0.0F, -25.0F, -15.0F, swingChargeProgress);
        AnimationUtils.lerpModelPartRotation(this.head, 10.0F, -15.0F, 0.0F, swingChargeProgress);
        this.lowerTorso.yaw = MathHelper.lerp(swingChargeProgress, 0.0F, -15.0F * RADIANS_PER_DEGREE);

        // Melee attack swing animation
        float swingReleaseProgress = entity.swingReleaseProgress.getValue();
        AnimationUtils.lerpModelPartRotation(this.leftArm, -60.0F, 40.0F, 25.0F, swingReleaseProgress);
        AnimationUtils.lerpModelPartRotation(this.rightArm, 20.0F, 0.0F, 0.0F, swingReleaseProgress);
        AnimationUtils.lerpModelPartRotation(this.upperTorso, 0.0F, 10.0F, -5.0F, swingReleaseProgress);
        AnimationUtils.lerpModelPartRotation(this.lowerTorso, 0.0F, 10.0F, 0.0F, swingReleaseProgress);
        AnimationUtils.lerpModelPartRotation(this.head, 5.0F, -15.0F, 0.0F, swingReleaseProgress);
        this.head.yaw = MathHelper.lerp(swingChargeProgress, this.head.yaw, -15.0F * RADIANS_PER_DEGREE);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
