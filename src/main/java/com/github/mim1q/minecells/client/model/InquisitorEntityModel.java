package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.entity.InquisitorEntity;
import com.github.mim1q.minecells.util.AnimationHelper;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class InquisitorEntityModel extends EntityModel<InquisitorEntity> {

    private final ModelPart root;
    private final ModelPart waist;
    private final ModelPart belt;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart upperTorso;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart head;

    public InquisitorEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.waist = this.root.getChild("waist");
        this.belt = this.waist.getChild("belt");
        this.rightLeg = this.waist.getChild("right_leg");
        this.leftLeg = this.waist.getChild("left_leg");
        this.upperTorso = this.waist.getChild("upper_torso");
        this.leftArm = this.upperTorso.getChild("left_arm");
        this.rightArm = this.upperTorso.getChild("right_arm");
        ModelPart neck = this.upperTorso.getChild("neck");
        this.head = neck.getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData dRoot = modelPartData.addChild("root",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData dWaist = dRoot.addChild("waist",
            ModelPartBuilder.create()
                .uv(48, 34)
                .cuboid(-3.0F, -3.0F, -1.5F, 6, 3, 3),
            ModelTransform.pivot(0.0F, -15.0F, 0.0F));

        dWaist.addChild("right_leg",
            ModelPartBuilder.create()
                .uv(0, 35)
                .cuboid(-2.0F, 0.0F, -2.0F, 4, 11, 4)
                .uv(42, 50)
                .cuboid(-1.5F, 11.0F, -1.5F, 3, 4, 3),
            ModelTransform.pivot(-2.0F, 0.0F, 0.0F));

        dWaist.addChild("left_leg",
            ModelPartBuilder.create()
                    .uv(38, 0)
                    .cuboid(-2.0F, 0.0F, -2.0F, 4, 11, 4)
                    .uv(51, 40)
                    .cuboid(-1.5F, 11.0F, -1.5F, 3, 4, 3),
            ModelTransform.pivot(2.0F, 0.0F, 0.0F));

        dWaist.addChild("belt",
            ModelPartBuilder.create()
                .uv(34,50)
                .cuboid(-2.0F, -0.0F, 0.0F, 4, 12, 0),
            ModelTransform.pivot(0.0F, 0.0F, -2.05F));

        ModelPartData dUpperTorso = dWaist.addChild("upper_torso",
            ModelPartBuilder.create()
                .uv(24, 32)
                .cuboid(-4.0F, -6.0F, -2.0F, 8, 6, 4),
            ModelTransform.pivot(0.0F, -3.0F, 0.0F));

        dUpperTorso.addChild("left_arm",
            ModelPartBuilder.create()
                .uv(10, 50)
                .cuboid(0.0F, -1.5F, -1.5F, 2, 14, 3)
                .uv(16, 42)
                .cuboid(-1.5F, -2.0F, -2.5F, 5, 3, 5)
                .uv(46, 15)
                .cuboid(-1.5F, 1.0F, -2.5F, 4, 2, 5)
                .uv(20, 50)
                .cuboid(-0.5F, 8.5F, -2.0F, 3, 2, 4),
            ModelTransform.pivot(4.5F, -4.5F, 0.0F));

        dUpperTorso.addChild("right_arm",
            ModelPartBuilder.create()
                .uv(0, 50)
                .cuboid(-2.0F, -1.5F, -1.5F, 2, 14, 3)
                .uv(36, 42)
                .cuboid(-3.5F, -2.0F, -2.5F, 5, 3, 5)
                .uv(47, 27)
                .cuboid(-2.5F, 1.0F, -2.5F, 4, 2, 5)
                .uv(20, 50)
                .cuboid(-2.5F, 8.5F, -2.0F, 3, 2, 4),
            ModelTransform.pivot(-4.5F, -4.5F, 0.0F));

        ModelPartData dNeck = dUpperTorso.addChild("neck",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, -6.0F, -1.0F));

        dNeck.addChild("head",
            ModelPartBuilder.create()
                .uv(28, 20)
                .cuboid(-3.0F, -6.0F, -3.0F, 6, 6, 6)
                .uv(0, 0)
                .cuboid(-9.5F, -19.0F, 3.51F, 19, 20, 0)
                .uv(0, 20)
                .cuboid(-3.5F, -8.0F, -3.5F, 7, 8, 7),
            ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(InquisitorEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        AnimationHelper.rotateHead(headYaw, headPitch, this.head);
        AnimationHelper.bipedWalk(limbAngle * 2.0F, limbDistance, this.root, this.rightLeg, this.leftLeg, this.rightArm, this.leftArm, this.upperTorso, this.waist);

        this.rightArm.roll = 30.0F * MathHelper.RADIANS_PER_DEGREE;
        this.leftArm.roll = -30.0F * MathHelper.RADIANS_PER_DEGREE;
        this.leftArm.pitch += -30.0F * MathHelper.RADIANS_PER_DEGREE;
        this.rightArm.pitch += -30.0F * MathHelper.RADIANS_PER_DEGREE;

        this.belt.pitch = -Math.abs(MathHelper.sin(limbAngle) * limbDistance) * 1.5F;

        // Shooting animation

        String animationState = entity.getAttackState();

        if (!animationState.equals(entity.lastAnimation)) {
            entity.animationTimestamp = animationProgress;
        }

        float timestamp = entity.animationTimestamp;

        float targetAdditionalRotation = 0.0F;
        float startAdditionalRotation = -MathHelper.PI * 0.2F;
        if (animationState.equals("shoot")) {
            startAdditionalRotation = 0.0F;
            targetAdditionalRotation = -MathHelper.PI * 0.2F;
        }

        float animationTime = animationProgress - timestamp;
        float additionalRotation = MathHelper.clampedLerp(
                startAdditionalRotation,
                targetAdditionalRotation,
                animationTime / 10.0F);

        this.leftArm.yaw = additionalRotation;
        this.rightArm.yaw = -additionalRotation;
        this.upperTorso.pitch = additionalRotation * 0.2F;

        entity.lastAnimation = animationState;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
