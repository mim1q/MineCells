package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.DisgustingWormEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class DisgustingWormEntityModel extends EntityModel<DisgustingWormEntity> {

    private final ModelPart head;
    private final ModelPart frontSegment;
    private final ModelPart middleSegment;
    private final ModelPart backSegment;

    float limbAngle = 0.0F;
    float limbDistance = 0.0F;

    public DisgustingWormEntityModel(ModelPart root) {
        root.pivotY = 24.0F;
        this.head = root.getChild("head");
        this.frontSegment = root.getChild("front_segment");
        this.middleSegment = root.getChild("middle_segment");
        this.backSegment = root.getChild("back_segment");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("head",
            ModelPartBuilder.create()
                .uv(0, 35)
                .cuboid(-5.0F, -3.0F, -4.0F, 10, 3, 4)
                .uv(32, 0)
                .cuboid(-5.0F, -10.0F, -4.0F, 10, 3, 4)
                .uv(43, 31)
                .cuboid(-5.0F, -7.0F, -4.0F, 3, 4, 4)
                .uv(16, 42)
                .cuboid(2.0F, -7.0F, -4.0F, 3, 4, 4)
                .uv(45, 50)
                .cuboid(-2.0F, -7.0F, -3.0F, 4, 4, 0)
                .uv(0, 29)
                .cuboid(-2.0F, -7.0F, -1.0F, 4, 4, 1),
            ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData dFrontSegment = modelPartData.addChild("front_segment",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-5.0F, -9.0F, 0.0F, 10, 9, 6),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        dFrontSegment.addChild("egg_1",
            ModelPartBuilder.create()
                .uv(22, 23)
                .cuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6),
            ModelTransform.of(
                3.0F, -10.0F, 3.0F,
                -5.0F * RADIANS_PER_DEGREE, 10.0F * RADIANS_PER_DEGREE, 10.0F * RADIANS_PER_DEGREE));

        dFrontSegment.addChild("egg_2",
            ModelPartBuilder.create()
                .uv(28, 35)
                .cuboid(-2.5F, -2.5F, -2.5F, 5, 5, 5),
            ModelTransform.of(
                -3.5F, -9.0F, 4.5F,
                0.0F, 45.0F * RADIANS_PER_DEGREE, 10.0F * RADIANS_PER_DEGREE));

        dFrontSegment.addChild("egg_3",
            ModelPartBuilder.create()
                .uv(44, 7)
                .cuboid(-1.5F, -1.5F, -1.5F, 3, 3, 3),
            ModelTransform.of(
                4.5F, -2.5F, 5.5F,
                10.0F * RADIANS_PER_DEGREE, -10.0F * RADIANS_PER_DEGREE, 0.0F));

        ModelPartData dMiddleSegment = modelPartData.addChild("middle_segment",
            ModelPartBuilder.create()
                .uv(0, 15)
                .cuboid(-4.0F, -8.0F, -0.0F, 8, 8, 6),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        dMiddleSegment.addChild("egg_4",
            ModelPartBuilder.create()
                .uv(28, 35)
                .cuboid(-2.5F, -2.5F, -2.75F, 5, 5, 5),
            ModelTransform.of(
                2.5F, -8.5F, 3.5F,
                0.0F, -35.0F * RADIANS_PER_DEGREE, 0.0F));

        dMiddleSegment.addChild("egg_5",
            ModelPartBuilder.create()
                .uv(0, 42)
                .cuboid(-2.0F, -2.0F, -2.0F, 4, 4, 4),
            ModelTransform.of(
                -2.5F, -8.5F, 3.0F,
                -20.0F * RADIANS_PER_DEGREE, 0.0F, -15.0F * RADIANS_PER_DEGREE));

        dMiddleSegment.addChild("egg_6",
            ModelPartBuilder.create()
                .uv(44, 7)
                .cuboid(-1.5F, -1.5F, -1.5F, 3, 3, 3),
            ModelTransform.of(
                -3.5F, -2.5F, 6.0F,
                -10.0F * RADIANS_PER_DEGREE, 15.0F * RADIANS_PER_DEGREE, 0.0F));

        ModelPartData dBackSegment = modelPartData.addChild("back_segment",
            ModelPartBuilder.create()
                .uv(26, 9)
                .cuboid(-3.0F, -5.0F, 0.0F, 6, 5, 6),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        dBackSegment.addChild("egg_7",
            ModelPartBuilder.create()
                .uv(30, 45)
                .cuboid(-1.5F, -1.5F, -1.5F, 3, 3, 3),
            ModelTransform.of(
                2.0F, -5.5F, 1.5F,
                -10.0F * RADIANS_PER_DEGREE, -15.0F * RADIANS_PER_DEGREE, 10.0F * RADIANS_PER_DEGREE));

        dBackSegment.addChild("egg_8",
            ModelPartBuilder.create()
                .uv(0, 42)
                .cuboid(-2.0F, -2.0F, -2.0F, 4, 4, 4),
            ModelTransform.of(
                -2.0F, -6.0F, 2.0F,
                -10.0F * RADIANS_PER_DEGREE, -20.0F * RADIANS_PER_DEGREE, 0.0F));

        dBackSegment.addChild("egg_9",
            ModelPartBuilder.create()
                .uv(44, 7)
                .cuboid(-1.5F, -1.5F, -1.5F, 3, 3, 3),
            ModelTransform.of(
                2.5F, -2.5F, 5.5F,
                -20.0F * RADIANS_PER_DEGREE, -35.0F * RADIANS_PER_DEGREE, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(DisgustingWormEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.limbAngle = limbAngle;
        this.limbDistance = limbDistance;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        float scale1 = MathHelper.sin(this.limbAngle) * this.limbDistance * 0.5F;
        matrices.translate(0.0F, 0.0F, -0.4F);
        matrices.scale(1.0F, 1.0F, 1.0F + scale1);
        this.frontSegment.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();

        matrices.push();
        float scale2 = MathHelper.sin(this.limbAngle + MathHelper.PI * 0.33F) * this.limbDistance * 0.5F;
        matrices.translate(0.0F, 0.0F, -0.025F + scale1 * 0.375F);
        matrices.scale(1.0F, 1.0F, 1.0F + scale2);
        this.middleSegment.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();

        matrices.push();
        float scale3 = MathHelper.sin(this.limbAngle + MathHelper.PI * 0.66F) * this.limbDistance * 0.5F;
        matrices.translate(0.0F, 0.0F, 0.35F + (scale1 + scale2) * 0.375F);
        matrices.scale(1.0F, 1.0F, 1.0F + scale3);
        this.backSegment.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();

        matrices.push();
        matrices.translate(0.0F, 1.5F, -0.4F);
        matrices.scale(1.0F, 1.0F + scale2 * 0.25F, 1.0F);
        this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
