package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.UndeadArcherEntity;
import com.github.mim1q.minecells.util.AnimationHelper;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class UndeadArcherEntityModel extends EntityModel<UndeadArcherEntity> implements ModelWithArms {

    private final ModelPart root;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart head;

    public UndeadArcherEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        ModelPart waist = this.root.getChild("waist");
        this.rightLeg = waist.getChild("right_leg");
        this.leftLeg = waist.getChild("left_leg");
        ModelPart torso = waist.getChild("torso");
        this.leftArm = torso.getChild("left_arm");
        this.rightArm = torso.getChild("right_arm");
        ModelPart neck = torso.getChild("neck");
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
                .uv(27, 11)
                .cuboid(-4.0F, -2.0F, -2.5F, 8, 2, 5),
            ModelTransform.pivot(0.0F, -13.0F, 0.0F));

        dWaist.addChild("right_leg",
            ModelPartBuilder.create()
                .uv(0, 46)
                .cuboid(-1.0F, -1.0F, -1.0F, 2, 14, 2)
                .uv(24, 40)
                .cuboid(-1.5F, 0.0F, -2.0F, 3, 9, 4),
            ModelTransform.pivot(-2.0F, 0.0F, 0.0F));

        dWaist.addChild("left_leg",
            ModelPartBuilder.create()
                .uv(8, 46)
                .cuboid(-1.0F, -1.0F, -1.0F, 2, 14, 2)
                .uv(50, 29)
                .cuboid(-1.5F, 0.0F, -2.0F, 3, 9, 4),
            ModelTransform.pivot(2.0F, 0.0F, 0.0F));

        ModelPartData dTorso = dWaist.addChild("torso",
            ModelPartBuilder.create()
                .uv(50, 0)
                .cuboid(-1.0F, -9.0F, -1.0F, 2, 10, 2)
                .uv(23, 29)
                .cuboid(-4.0F, -9.0F, -3.5F, 8, 6, 5),
            ModelTransform.pivot(0.0F, -2.0F, 0.5F));

        dTorso.addChild("left_arm",
            ModelPartBuilder.create()
                .uv(16, 46)
                .cuboid(0.25F, -1.0F, -1.0F, 2, 12, 2)
                .uv(30, 0)
                .cuboid(-0.5F, -2.0F, -1.5F, 3, 5, 3),
            ModelTransform.pivot(4.0F, -8.0F, -1.0F));

        dTorso.addChild("right_arm",
            ModelPartBuilder.create()
                .uv(45, 18)
                .cuboid(-2.0F, -1.0F, -1.0F, 2, 12, 2)
                .uv(33, 21)
                .cuboid(-2.5F, -2.0F, -1.5F, 3, 5, 3),
            ModelTransform.pivot(-4.0F, -8.0F, -1.0F));

        dTorso.addChild("scarf",
            ModelPartBuilder.create()
                .uv(0, 11)
                .cuboid(-4.5F, -2.0F, -4.5F, 9, 4, 9),
            ModelTransform.of(0.0F, -9.0F, -1.0F, 30.0F * RADIANS_PER_DEGREE, 0.0F, 0.0F));

        ModelPartData dNeck = dTorso.addChild("neck",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, -9.0F, -2.0F));

        dNeck.addChild("head",
            ModelPartBuilder.create()
                .uv(0, 34)
                .cuboid(-3.0F, -6.0F, -3.0F, 6, 6, 6)
                .uv(36, 51)
                .cuboid(-3.5F, -6.0F, -3.5F, 7, 6, 7)
                .uv(0, 0)
                .cuboid(-5.0F, -7.0F, -5.0F, 10, 1, 10)
                .uv(0, 24)
                .cuboid(-3.0F, -9.0F, -3.0F, 6, 2, 8),
            ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(UndeadArcherEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        AnimationHelper.rotateHead(headYaw, headPitch, this.head);
        AnimationHelper.bipedWalk(limbAngle, limbDistance, this.root, this.rightLeg, this.leftLeg, this.rightArm, this.leftArm, null, null);
        this.leftArm.roll = 0.0F;
        this.leftArm.yaw = 0.0F;
        this.rightArm.pitch *= 0.2F;
        this.rightArm.pitch -= 60.0F * RADIANS_PER_DEGREE;
        this.rightArm.yaw = 0.0F;
        this.leftArm.pivotZ = -1.0F;

        String animation = "idle";
        if (entity.isShootCharging()) {
            animation = "charge";
        } else if (entity.isShootReleasing()) {
            animation = "release";
        }

        if (!animation.equals(entity.lastAnimation)) {
            entity.animationTimestamp = 0;
            entity.animationTimestamp = animationProgress;
        }

        float animationTime = animationProgress - entity.animationTimestamp;

        float rightArmPitch = -90.0F * RADIANS_PER_DEGREE;
        float rightArmYaw = -15.0F * RADIANS_PER_DEGREE;
        float leftArmPitch = -90.0F * RADIANS_PER_DEGREE;
        float leftArmYaw = 30.0F * RADIANS_PER_DEGREE;

        float delta = 0;

        if (entity.isShootCharging()) {
            delta = animationTime / 5.0F;
            this.leftArm.pivotZ = MathHelper.clampedLerp(this.leftArm.pivotZ, 2.0F, (animationTime - 5) / 15.0F);
        } else if (entity.isShootReleasing()) {
            delta = 1 - animationTime / 5.0F;
        }

        this.rightArm.pitch = MathHelper.clampedLerp(this.rightArm.pitch, rightArmPitch, delta);
        this.rightArm.yaw += MathHelper.clampedLerp(this.rightArm.yaw, rightArmYaw, delta);
        this.leftArm.pitch = MathHelper.clampedLerp(this.leftArm.pitch, leftArmPitch, delta);
        this.leftArm.yaw += MathHelper.clampedLerp(this.leftArm.yaw, leftArmYaw, delta);

        entity.lastAnimation = animation;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        matrices.translate(0.0F, 0.5F, 0.0F);
        if (arm == Arm.RIGHT) {
            this.rightArm.rotate(matrices);
        } else {
            this.leftArm.rotate(matrices);
        }
    }
}
