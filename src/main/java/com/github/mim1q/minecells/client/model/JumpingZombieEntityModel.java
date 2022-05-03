package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class JumpingZombieEntityModel extends EntityModel<JumpingZombieEntity> {

    protected String currentAnimation = "none";

    private final ModelPart root;
    private final ModelPart waist;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart lowerTorso;
    private final ModelPart upperTorso;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart neck;
    private final ModelPart head;

    public JumpingZombieEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.waist = this.root.getChild("waist");
        this.leftLeg = this.waist.getChild("left_leg");
        this.rightLeg = this.waist.getChild("right_leg");
        this.lowerTorso = this.waist.getChild("lower_torso");
        this.upperTorso = this.lowerTorso.getChild("upper_torso");
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
            ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData dWaist = dRoot.addChild("waist",
            ModelPartBuilder.create()
                .uv(20, 20)
                .cuboid(-4.0F, 0.0F, -2.0F, 8, 3, 4),
            ModelTransform.pivot(0.0F, -15.0F, 0.0F));

        dWaist.addChild("left_leg",
            ModelPartBuilder.create()
                .uv(26, 0)
                .cuboid(0.0F, -1.0F, 0.0F, 3, 14, 3),
            ModelTransform.pivot(0.5F, 2.0F, -1.5F));

        dWaist.addChild("right_leg",
            ModelPartBuilder.create()
                .uv(0, 24)
                .cuboid(0.0F, -1.0F, 0.0F, 3, 14, 3),
            ModelTransform.pivot(-3.5F, 2.0F, -1.5F));

        ModelPartData dLowerTorso = dWaist.addChild("lower_torso",
            ModelPartBuilder.create()
                .uv(32, 27)
                .cuboid(-3.0F, -5.0F, -1.5F, 6, 6, 3),
            ModelTransform.NONE);

        ModelPartData dUpperTorso = dLowerTorso.addChild("upper_torso",
            ModelPartBuilder.create()
                .uv(0, 1)
                .cuboid(-4.0F, -4.0F, -2.5F, 8.0F, 5.0F, 5.0F),
            ModelTransform.pivot(0.0F, -5.0F, 0.0F));

        dUpperTorso.addChild("left_arm",
            ModelPartBuilder.create()
                .uv(22, 27)
                .cuboid(0.0F, -1.5F, 0.0F, 2, 12, 3),
            ModelTransform.pivot(4.0F, -2.5F, -1.5F));

        dUpperTorso.addChild("right_arm",
                ModelPartBuilder.create()
                    .uv(22, 27)
                    .cuboid(-2.0F, -1.5F, 0.0F, 2, 12, 3),
                ModelTransform.pivot(-4.0F, -2.5F, -1.5F));

        ModelPartData dNeck = dUpperTorso.addChild("neck",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -4.0F, -0.5F));

        dNeck.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 11)
                        .cuboid(-3.0F, -7.0F, -3.0F, 6, 7, 6),
                ModelTransform.NONE);

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(JumpingZombieEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.rightLeg.pitch = MathHelper.sin(limbAngle / 3.0F) * limbDistance;
        this.leftLeg.pitch = -MathHelper.sin(limbAngle / 3.0F) * limbDistance;
        this.leftArm.pitch = -20.0F * MathHelper.RADIANS_PER_DEGREE + this.rightLeg.pitch;
        this.rightArm.pitch = -20.0F * MathHelper.RADIANS_PER_DEGREE + this.leftLeg.pitch;
        this.lowerTorso.pitch = (10.0F + MathHelper.sin(limbAngle / 1.5F) * limbDistance * 10.0F) * MathHelper.RADIANS_PER_DEGREE;
        this.upperTorso.pitch = (10.0F + MathHelper.sin(animationProgress / 6.0F) * 5.0F) * MathHelper.RADIANS_PER_DEGREE;
        this.upperTorso.pitch += MathHelper.sin((limbAngle - MathHelper.PI / 4.0F) / 1.5F) * limbDistance * 10.0 * MathHelper.RADIANS_PER_DEGREE;
        this.upperTorso.roll = (MathHelper.sin((limbAngle + MathHelper.PI) / 3.0F) * limbDistance * 10.0F) * MathHelper.RADIANS_PER_DEGREE;
        this.neck.pitch = -15.0F * MathHelper.RADIANS_PER_DEGREE;
        this.head.pitch = headPitch * MathHelper.RADIANS_PER_DEGREE;
        this.head.yaw = headYaw * MathHelper.RADIANS_PER_DEGREE;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
