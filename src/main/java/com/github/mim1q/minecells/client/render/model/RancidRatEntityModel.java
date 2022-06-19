package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.RancidRatEntity;
import com.github.mim1q.minecells.util.AnimationHelper;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class RancidRatEntityModel extends EntityModel<RancidRatEntity> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart[] tail = new ModelPart[3];

    public RancidRatEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.leftHindLeg = this.body.getChild("left_hind_leg");
        this.rightHindLeg = this.body.getChild("right_hind_leg");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.tail[0] = this.body.getChild("tail_0");
        this.tail[1] = this.tail[0].getChild("tail_1");
        this.tail[2] = this.tail[1].getChild("tail_2");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData dRoot = modelPartData.addChild("root",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F)
        );

        ModelPartData dBody = dRoot.addChild("body",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-3.0F, -5.0F, -5.0F, 6, 5, 10)
                .uv(0, 15)
                .cuboid(-4.0F, -9.0F, 0.5F, 5, 5, 5)
                .uv(28, 15)
                .cuboid(1.0F, -6.0F, 1.5F, 3, 3, 3)
                .cuboid(-4.5F, -6.0F, -3.0F, 3, 3, 3)
                .uv(22, 0)
                .cuboid(0.0F, -7.5F, -4.0F, 4, 4, 4),
            ModelTransform.pivot(0.0F, -3.0F, 3.0F)
        );

        dBody.addChild("head",
            ModelPartBuilder.create()
                .uv(14, 19)
                .cuboid(-2.0F, -2.0F, -6.0F, 4, 4, 6)
                .uv(14, 29)
                .cuboid(-3.5F, -4.0F, -1.0F, 7, 4, 0, new Dilation(0.001F)),
            ModelTransform.pivot(0.0F, -2.0F, -5.0F)
        );

        dBody.addChild("left_hind_leg",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-1.0F, 0.0F, -1.0F, 2, 3, 2),
            ModelTransform.pivot(-2.0F, 0.0F, 3.0F)
        );

        dBody.addChild("right_hind_leg",
            ModelPartBuilder.create()
                .uv(0, 0)
                .mirrored()
                .cuboid(-1.0F, 0.0F, -1.0F, 2, 3, 2),
            ModelTransform.pivot(2.0F, 0.0F, 3.0F)
        );

        dBody.addChild("left_front_leg",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-1.0F, 0.0F, -1.0F, 2, 3, 2),
            ModelTransform.pivot(-2.0F, 0.0F, -4.0F)
        );

        dBody.addChild("right_front_leg",
            ModelPartBuilder.create()
                .uv(0, 0)
                .mirrored()
                .cuboid(-1.0F, 0.0F, -1.0F, 2, 3, 2),
            ModelTransform.pivot(2.0F, 0.0F, -4.0F)
        );

        ModelPartData dTail0 = dBody.addChild("tail_0",
            ModelPartBuilder.create()
                .uv(0, 25)
                .cuboid(-0.5F, -0.5F, 0.0F, 1, 1, 6),
            ModelTransform.pivot(0.0F, -3.5F, 5.0F)
        );

        ModelPartData dTail1 = dTail0.addChild("tail_1",
            ModelPartBuilder.create()
                .uv(0, 25)
                .cuboid(-0.5F, -0.5F, 0.0F, 1, 1, 6),
            ModelTransform.pivot(0.0F, 0.0F, 6.0F)
        );

        dTail1.addChild("tail_2",
            ModelPartBuilder.create()
                .uv(0, 25)
                .cuboid(-0.5F, -0.5F, 0.0F, 1, 1, 6),
            ModelTransform.pivot(0.0F, 0.0F, 6.0F)
        );

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(RancidRatEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        AnimationHelper.rotateHead(headYaw, headPitch, this.head);
        ratWalk(limbAngle, limbDistance, this.root, this.body, this.leftHindLeg, this.rightHindLeg, this.leftFrontLeg, this.rightFrontLeg);

        float multiplier = 1.0F - limbDistance;
        for (int i = 0; i < 3; i++) {
            tail[i].pitch = (float)Math.sin(animationProgress * 0.5F - i) * 10.0F * RADIANS_PER_DEGREE * limbDistance;
            tail[i].pitch += (float)Math.sin(animationProgress * 0.25F - i) * 5.0F * RADIANS_PER_DEGREE * multiplier;
        }
        tail[0].pitch -= 25.0F * RADIANS_PER_DEGREE * multiplier;
        tail[1].pitch += 15.0F * RADIANS_PER_DEGREE * multiplier;
        tail[2].pitch += 15.0F * RADIANS_PER_DEGREE * multiplier;

        float torsoRotation = entity.isLeapCharging() ? -30.0F : 0.0F;
        torsoRotation *= RADIANS_PER_DEGREE;
        entity.torsoRotation.setupTransitionTo(torsoRotation, 5.0F);
        entity.torsoRotation.update(animationProgress);

        this.body.pitch += entity.torsoRotation.getValue();
        this.head.pitch -= entity.torsoRotation.getValue();
        this.tail[0].pitch -= entity.torsoRotation.getValue();
    }

    public static void ratWalk(float limbAngle, float limbDistance, ModelPart root, ModelPart body, ModelPart leftHindLeg, ModelPart rightHindLeg, ModelPart leftFrontLeg, ModelPart rightFrontLeg) {
        rightFrontLeg.pitch = MathHelper.sin(limbAngle) * limbDistance * 45.0F * RADIANS_PER_DEGREE;
        leftFrontLeg.pitch = -rightFrontLeg.pitch;
        rightHindLeg.pitch = -rightFrontLeg.pitch;
        leftHindLeg.pitch = rightFrontLeg.pitch;
        body.pitch = MathHelper.sin(limbAngle) * limbDistance * 10.0F * RADIANS_PER_DEGREE;
        root.pivotY = 24.0F - MathHelper.abs(MathHelper.sin(limbAngle * 0.5F)) * limbDistance;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
