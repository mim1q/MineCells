package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.ScorpionEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ScorpionEntityModel extends EntityModel<ScorpionEntity>  {

    private final ModelPart root;
    private final ModelPart[] tail = new ModelPart[5];
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    private boolean shouldRender = false;

    public ScorpionEntityModel(ModelPart bone) {
        this.root = bone.getChild("root");
        this.body = this.root.getChild("body");
        this.tail[0] = body.getChild("tail_0");
        for (int i = 1; i < tail.length; i++) {
            this.tail[i] = this.tail[i - 1].getChild("tail_" + i);
        }
        this.head = this.body.getChild("head");
        this.leftHindLeg = this.root.getChild("left_hind_leg");
        this.leftFrontLeg = this.root.getChild("left_front_leg");
        this.rightHindLeg = this.root.getChild("right_hind_leg");
        this.rightFrontLeg = this.root.getChild("right_front_leg");
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
                .cuboid(-4.0F, -6.0F, -6.0F, 8, 6, 12),
            ModelTransform.pivot(0.0F, -4.0F, 0.0F)
        );

        dBody.addChild("plate_0",
            ModelPartBuilder.create()
                .uv(28, 0)
                .cuboid(-4.0F, 0.0F, 0.0F, 8, 2, 9, new Dilation(0.02F)),
            ModelTransform.of(0.0F, -6.0F, -6.0F, MathUtils.radians(40.0F), 0.0F, 0.0F)
        );

        dBody.addChild("plate_1",
            ModelPartBuilder.create()
                .uv(28, 0)
                .cuboid(-4.0F, 0.0F, 0.0F, 8, 2, 9, new Dilation(0.01F)),
            ModelTransform.of(0.0F, -6.0F, -3.0F, MathUtils.radians(25.0F), 0.0F, 0.0F)
        );

        dRoot.addChild("left_front_leg",
            ModelPartBuilder.create()
                .uv(0, 14)
                .cuboid(0.0F, 0.0F, -2.0F, 0, 9, 4, new Dilation(0.01F)),
            ModelTransform.of(4.5F, -9.0F, -5.5F, 0.0F, MathUtils.radians(25.0F), 0.0F)
        );

        dRoot.addChild("left_hind_leg",
            ModelPartBuilder.create()
                .uv(0, 14)
                .cuboid(0.0F, 0.0F, -2.0F, 0, 9, 4, new Dilation(0.01F)),
            ModelTransform.of(4.5F, -9.0F, 5.5F, 0.0F, MathUtils.radians(-25.0F), 0.0F)
        );

        dRoot.addChild("right_front_leg",
            ModelPartBuilder.create()
                .uv(0, 14)
                .cuboid(0.0F, 0.0F, -2.0F, 0, 9, 4, new Dilation(0.01F)),
            ModelTransform.of(-4.5F, -9.0F, -5.5F, 0.0F, MathUtils.radians(-25.0F), 0.0F)
        );

        dRoot.addChild("right_hind_leg",
            ModelPartBuilder.create()
                .uv(0, 14)
                .cuboid(0.0F, 0.0F, -2.0F, 0, 9, 4, new Dilation(0.01F)),
            ModelTransform.of(-4.5F, -9.0F, 5.5F, 0.0F, MathUtils.radians(25.0F), 0.0F)
        );

        ModelPartData dHead = dBody.addChild("head",
            ModelPartBuilder.create()
                .uv(26, 37)
                .cuboid(-3.5F, -3.5F, -5.0F, 7, 7, 5),
            ModelTransform.pivot(0.0F, -3.0F, -6.0F)
        );

        ModelPartData dUpperRightMandible = dHead.addChild("upper_right_mandible",
            ModelPartBuilder.create()
                .uv(0, 45)
                .cuboid(-1.0F, -2.0F, -1.0F, 2, 3, 1),
            ModelTransform.of(-2.5F, -3.0F, -5.0F, MathUtils.radians(45.0F), 0.0F, MathUtils.radians(-30.0F))
        );

        dUpperRightMandible.addChild("upper_right_spike",
            ModelPartBuilder.create()
                .uv(6, 45)
                .cuboid(-0.5F, -2.0F, -1.0F, 1, 2, 1),
            ModelTransform.of(0.0F, -2.0F, 0.0F, MathUtils.radians(45.0F), 0.0F, 0.0F)
        );

        ModelPartData dUpperLeftMandible = dHead.addChild("upper_left_mandible",
            ModelPartBuilder.create()
                .uv(0, 45)
                .cuboid(-1.0F, -2.0F, -1.0F, 2, 3, 1),
            ModelTransform.of(2.5F, -3.0F, -5.0F, MathUtils.radians(45.0F), 0.0F, MathUtils.radians(30.0F))
        );

        dUpperLeftMandible.addChild("upper_left_spike",
            ModelPartBuilder.create()
                .uv(6, 45)
                .cuboid(-0.5F, -2.0F, -1.0F, 1, 2, 1),
            ModelTransform.of(0.0F, -2.0F, 0.0F, MathUtils.radians(45.0F), 0.0F, 0.0F)
        );

        ModelPartData dLowerLeftMandible = dHead.addChild("lower_left_mandible",
            ModelPartBuilder.create()
                .uv(0, 45).mirrored()
                .cuboid(-1.0F, -1.0F, -1.0F, 2, 3, 1),
            ModelTransform.of(2.5F, 3.0F, -5.0F, MathUtils.radians(-45.0F), 0.0F, MathUtils.radians(-30.0F))
        );

        dLowerLeftMandible.addChild("lower_left_spike",
            ModelPartBuilder.create()
                .uv(6, 45)
                .cuboid(-0.5F, 0.0F, -1.0F, 1, 2, 1),
            ModelTransform.of(0.0F, 2.0F, 0.0F, MathUtils.radians(-45.0F), 0.0F, 0.0F)
        );

        ModelPartData dLowerRightMandible = dHead.addChild("lower_right_mandible",
            ModelPartBuilder.create()
                .uv(0, 45).mirrored()
                .cuboid(-1.0F, -1.0F, -1.0F, 2, 3, 1),
            ModelTransform.of(-2.5F, 3.0F, -5.0F, MathUtils.radians(-45.0F), 0.0F, MathUtils.radians(30.0F))
        );

        dLowerRightMandible.addChild("lower_right_spike",
            ModelPartBuilder.create()
                .uv(6, 45)
                .cuboid(-0.5F, 0.0F, -1.0F, 1, 2, 1),
            ModelTransform.of(0.0F, 2.0F, 0.0F, MathUtils.radians(-45.0F), 0.0F, 0.0F)
        );

        ModelPartData dTail0 = dBody.addChild("tail_0",
            ModelPartBuilder.create()
                .uv(0, 18)
                .cuboid(-3.0F, -5.0F, 0.0F, 6, 5, 10, new Dilation(0.02F)),
            ModelTransform.pivot(0.0F, -1.0F, 6.0F)
        );

        ModelPartData dTail1 = dTail0.addChild("tail_1",
            ModelPartBuilder.create()
                .uv(0, 18).mirrored()
                .cuboid(-3.0F, -5.0F, 0.0F, 6, 5, 10, new Dilation(0.01F)),
            ModelTransform.pivot(0.0F, 0.0F, 10.0F)
        );

        ModelPartData dTail2 = dTail1.addChild("tail_2",
            ModelPartBuilder.create()
                .uv(22, 23)
                .cuboid(-3.0F, -4.0F, 0.0F, 6, 4, 10),
            ModelTransform.pivot(0.0F, 0.0F, 10.0F)
        );

        ModelPartData dTail3 = dTail2.addChild("tail_3",
            ModelPartBuilder.create()
                .uv(34, 12)
                .cuboid(-2.5F, -4.0F, 0.0F, 5, 4, 6),
            ModelTransform.pivot(0.0F, 0.0F, 10.0F)
        );

        dTail3.addChild("tail_4",
            ModelPartBuilder.create()
                .uv(0, 33)
                .cuboid(-3.0F, -5.0F, 0.0F, 6, 6, 6)
                .uv(0, 0)
                .cuboid(0.0F, -5.0F, 6.0F, 0, 5, 3, new Dilation(0.01F)),
            ModelTransform.pivot(0.0F, 0.0F, 6.0F)
        );

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(ScorpionEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        AnimationUtils.rotateHead(headYaw, headPitch, this.head);

        this.shouldRender = !entity.isSleeping();

        // Update animation properties
        if (!entity.isSleeping()) {
            entity.buriedProgress.setupTransitionTo(0.0F, 20);
        }

        if (entity.getDataTracker().get(ScorpionEntity.SHOOT_CHARGING)) {
            entity.swingProgress.setupTransitionTo(1.0F, 20);
        } else {
            entity.swingProgress.setupTransitionTo(0.0F, 5);
        }

        entity.buriedProgress.update(animationProgress);
        entity.swingProgress.update(animationProgress);

        this.root.pivotY = 24.0F + entity.buriedProgress.getValue() * 24.0F;

        // Walking animation
        this.tail[0].pitch = MathUtils.radians(55.0F);
        this.tail[1].pitch = MathUtils.radians(30.0F);
        this.tail[2].pitch = MathUtils.radians(30.0F);
        this.tail[3].pitch = MathUtils.radians(45.0F);
        this.tail[4].pitch = MathUtils.radians(35.0F);

        for (int i = 0; i < 5; i++) {
            float deltaPitch = MathHelper.sin(limbAngle * 0.5F) * limbDistance;
            this.tail[i].pitch += deltaPitch * MathUtils.radians(5.0F);
            float deltaRoll = MathHelper.sin(limbAngle * 0.25F) * limbDistance;
            this.tail[i].roll = deltaRoll * MathUtils.radians(5.0F);
            this.tail[i].yaw = deltaRoll * MathUtils.radians(5.0F);

            this.tail[i].pitch = MathUtils.easeInOutQuad(this.tail[i].pitch, MathUtils.radians(60.0F), entity.buriedProgress.getValue());
        }
        float deltaPivotY = MathHelper.sin(limbAngle * 0.5F) * limbDistance * 3.0F;
        float deltaPivotZ = -MathHelper.cos(limbAngle * 0.5F) * limbDistance * 2.0F;
        this.leftHindLeg.pivotY = -9.0F - Math.max(0.0F, deltaPivotY);
        this.rightHindLeg.pivotY = -9.0F - Math.max(0.0F, -deltaPivotY);
        this.leftFrontLeg.pivotY = this.rightHindLeg.pivotY;
        this.rightFrontLeg.pivotY = this.leftHindLeg.pivotY;
        this.leftHindLeg.pivotZ = 5.5F - deltaPivotZ;
        this.rightHindLeg.pivotZ = 5.5F + deltaPivotZ;
        this.leftFrontLeg.pivotZ = -5.5F + deltaPivotZ;
        this.rightFrontLeg.pivotZ = -5.5F - deltaPivotZ;

        float swingProgress = entity.swingProgress.getValue();
        float shake = MathHelper.sin(animationProgress * 10.0F);

        this.body.pitch = MathUtils.lerp(0.0F, MathUtils.radians(-30.0F), swingProgress);
        this.head.pitch -= MathUtils.lerp(0.0F, MathUtils.radians(-30.0F + shake * 5.0F), swingProgress);

        for (ModelPart part : this.tail) {
            part.pitch = MathUtils.lerp(
                part.pitch,
                MathUtils.radians(15.0F),
                swingProgress
            );
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (shouldRender) {
            this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }
}
