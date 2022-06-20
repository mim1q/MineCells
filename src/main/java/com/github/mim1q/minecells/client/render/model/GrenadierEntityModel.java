package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.GrenadierEntity;
import com.github.mim1q.minecells.util.animation.AnimationHelper;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class GrenadierEntityModel extends EntityModel<GrenadierEntity> {

    private final ModelPart root;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart lowerTorso;
    private final ModelPart upperTorso;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart[] balls = new ModelPart[5];
    private static final float[] ballOffsets = { -5.0F, -3.0F, -3.0F, 2.0F, 2.0F };

    public GrenadierEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.lowerTorso = this.root.getChild("lower_torso");
        this.upperTorso = this.lowerTorso.getChild("upper_torso");
        this.leftArm = this.upperTorso.getChild("left_arm");
        this.rightArm = this.upperTorso.getChild("right_arm");
        this.neck = this.upperTorso.getChild("neck");
        this.head = this.neck.getChild("head");

        for (int i = 0; i < 5; i++) {
            this.balls[i] = this.upperTorso.getChild("ball_" + i);
        }
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData dRoot = modelPartData.addChild("root",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        dRoot.addChild("left_leg",
            ModelPartBuilder.create()
                .uv(0, 37)
                .cuboid(-1.5F, -1.0F, -1.5F, 3, 14, 3)
                .uv(16, 56)
                .cuboid(-2.0F, 9.0F, -2.0F, 4, 2, 4),
            ModelTransform.pivot(2.0F, -13.0F, 0.0F));

        dRoot.addChild("right_leg",
            ModelPartBuilder.create()
                .uv(27,34)
                .cuboid(-1.5F, -1.0F, -1.5F, 3, 14, 3)
                .uv(0, 56)
                .cuboid(-2.0F, 9.0F, -2.0F, 4, 2, 4),
            ModelTransform.pivot(-2.0F, -13.0F, 0.0F));

        dRoot.addChild("waist",
            ModelPartBuilder.create()
                .uv(24, 0)
                .cuboid(-4.5F, -3.0F, -2.5F, 9, 3, 5),
            ModelTransform.pivot(0.0F, -12.0F, 0.0F));

        ModelPartData dLowerTorso = dRoot.addChild("lower_torso",
            ModelPartBuilder.create()
                .uv(39, 34)
                .cuboid(-3.0F, -5.0F, -1.5F, 6, 6, 3)
                .uv(52, 0)
                .cuboid(-1.5F, -3.0F, -3.0F, 3, 3, 3),
            ModelTransform.pivot(-0.0F, -15.0F, 0.0F));

        ModelPartData dUpperTorso = dLowerTorso.addChild("upper_torso",
            ModelPartBuilder.create()
                .uv(0, 26)
                .cuboid(-5.0F, -6.0F, -2.5F, 10, 6, 5)
                .uv(0, 16)
                .cuboid(-4.0F, -6.5F, -3.5F, 8, 2, 7),
            ModelTransform.pivot(0.0F, -4.0F, 0.0F));

        ModelPartData dNeck = dUpperTorso.addChild("neck",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, -5.0F, -1.0F));

        dNeck.addChild("head",
            ModelPartBuilder.create()
                .uv(30, 22)
                .cuboid(-3.0F, -6.0F, -3.0F, 6, 6, 6),
            ModelTransform.NONE);

        dUpperTorso.addChild("left_arm",
            ModelPartBuilder.create()
                .uv(39, 43)
                .cuboid(0.0F, -1.5F, -1.5F, 2, 16, 3),
            ModelTransform.pivot(5.0F, -3.5F, 0.0F));

        dUpperTorso.addChild("right_arm",
            ModelPartBuilder.create()
                .uv(12, 37)
                .cuboid(-2.0F, -1.5F, -1.5F, 2, 16, 3),
            ModelTransform.pivot(-5.0F, -3.5F, 0.0F));

        dUpperTorso.addChild("ball_0",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8),
            ModelTransform.of(
                0.5F, -5.0F, 7.5F,
                -10.0F * RADIANS_PER_DEGREE, 0.0F, 0.0F
            ));

        dUpperTorso.addChild("ball_1",
            ModelPartBuilder.create()
                .uv(26, 10)
                .cuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6),
            ModelTransform.of(
                5.0F, -3.0F, 6.0F,
                10.0F * RADIANS_PER_DEGREE, 25.0F * RADIANS_PER_DEGREE, 0.0F
            ));

        dUpperTorso.addChild("ball_2",
            ModelPartBuilder.create()
                .uv(26, 10)
                .cuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6),
            ModelTransform.of(
                -5.0F, -3.0F, 6.0F,
                10.0F * RADIANS_PER_DEGREE, -25.0F * RADIANS_PER_DEGREE, 0.0F
            ));

        dUpperTorso.addChild("ball_3",
            ModelPartBuilder.create()
                .uv(26, 10)
                .cuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6),
            ModelTransform.of(
                3.5F, 2.0F, 3.5F,
                -60.0F * RADIANS_PER_DEGREE, 10.0F * RADIANS_PER_DEGREE, -15.0F * RADIANS_PER_DEGREE
            ));

        dUpperTorso.addChild("ball_4",
            ModelPartBuilder.create()
                .uv(26, 10)
                .cuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6),
            ModelTransform.of(
                -3.5F, 2.0F, 3.5F,
                -45.0F * RADIANS_PER_DEGREE, -20.0F * RADIANS_PER_DEGREE, 15.0F * RADIANS_PER_DEGREE
            ));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(GrenadierEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        AnimationHelper.rotateHead(headYaw, headPitch, this.head);
        AnimationHelper.bipedWalk(limbAngle, limbDistance, this.root, this.rightLeg, this.leftLeg, this.rightArm, this.leftArm, this.lowerTorso, this.upperTorso);

        this.neck.pitch = -30.0F * RADIANS_PER_DEGREE;

        this.upperTorso.pitch += 20.0F * RADIANS_PER_DEGREE;
        this.lowerTorso.pitch += 10.0F * RADIANS_PER_DEGREE;

        this.leftArm.pitch -= 30.0F * RADIANS_PER_DEGREE;
        this.rightArm.pitch -= 30.0F * RADIANS_PER_DEGREE;

        // Balls ;)

        for (int i = 0; i < 5; i++) {
            balls[i].pivotY = ballOffsets[i] + MathHelper.sin(animationProgress * 0.25F + i * 0.2F) * 1.75F;
        }

        // Throwing animation

        String animationState = "idle";
        if (entity.isShootCharging()) { animationState = "shoot"; }

        if (!animationState.equals(entity.lastAnimation)) {
            entity.animationTimestamp = animationProgress;
        }
        float timestamp = entity.animationTimestamp;

        float targetAdditionalRotation = 0.0F;
        float startAdditionalRotation = -MathHelper.PI * 1.25F;
        if (animationState.equals("shoot")) {
            startAdditionalRotation = 0.0F;
            targetAdditionalRotation = -MathHelper.PI * 1.25F;
        }

        float animationTime = animationProgress - timestamp;
        entity.additionalRotation = MathHelper.clampedLerp(
                startAdditionalRotation,
                targetAdditionalRotation,
                animationTime * 0.15F
                );

        this.leftArm.pitch += entity.additionalRotation;
        this.rightArm.pitch += entity.additionalRotation;
        this.lowerTorso.pitch += entity.additionalRotation * 0.1F;
        this.upperTorso.pitch += entity.additionalRotation * 0.1F;

        entity.lastAnimation = animationState;

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
