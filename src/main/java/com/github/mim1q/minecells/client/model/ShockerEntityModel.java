package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.entity.ShockerEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ShockerEntityModel extends EntityModel<ShockerEntity> {

    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart eye;
    private final ModelPart rightFloater;
    private final ModelPart leftFloater;
    private final ModelPart bottomFloater;

    public ShockerEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.base = this.root.getChild("base");
        ModelPart eyeRim = this.base.getChild("eye_rim");
        this.eye = eyeRim.getChild("eye");
        this.rightFloater = this.root.getChild("right_floater");
        this.leftFloater = this.root.getChild("left_floater");
        this.bottomFloater = this.root.getChild("bottom_floater");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData dRoot = modelPartData.addChild("root",
            ModelPartBuilder.create(),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData dBase = dRoot.addChild("base",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-7.0F, -22.0F, -7.0F, 14, 22, 14),
            ModelTransform.pivot(0.0F, -5.0F, 0.0F));

        dBase.addChild("base_ring",
            ModelPartBuilder.create()
                .uv(0, 36)
                .cuboid(-8.0F, -4.0F, -8.0F, 16, 4, 16),
            ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        dBase.addChild("base_top",
            ModelPartBuilder.create()
                .uv(0, 56)
                .cuboid(-6.0F, -16.0F, -6.0F, 12, 16, 12),
            ModelTransform.pivot(0.0F, -22.0F, 0.0F));

        ModelPartData dEyeRim = dBase.addChild("eye_rim",
            ModelPartBuilder.create()
                .uv(42, 4)
                .cuboid(-5.0F, 3.0F, 0.0F, 10, 2, 2)
                .uv(42, 0)
                .cuboid(-5.0F, -5.0F, 0.0F, 10, 2, 2)
                .uv(6, 6)
                .cuboid(-5.0F, -3.0F, 0.0F, 2, 6, 2)
                .uv(0 ,0)
                .cuboid(3.0F, -3.0F, 0.0F, 2, 6, 2),
            ModelTransform.pivot(0.0F, -12.0F, -9.0F));

        dEyeRim.addChild("eye",
            ModelPartBuilder.create()
                .uv(0, 36)
                .cuboid(-1.0F, -1.0F, 1.0F, 2, 2, 2),
            ModelTransform.NONE);

        dRoot.addChild("bottom_floater",
            ModelPartBuilder.create()
                .uv(48, 20)
                .cuboid(-8.0F, -3.0F, -8.0F, 16, 3, 16),
            ModelTransform.NONE);

        dRoot.addChild("left_floater",
            ModelPartBuilder.create()
                .uv(48, 58)
                .cuboid(-1.5F, -6.0F, -4.0F, 3, 12, 8),
            ModelTransform.of(
                    15.0F, -32.0F, 0.0F,
                    0.0F, -20.0F * MathHelper.RADIANS_PER_DEGREE, -15.0F * MathHelper.RADIANS_PER_DEGREE)
            );

        dRoot.addChild("right_floater",
            ModelPartBuilder.create()
                .uv(62, 70)
                .cuboid(-1.5F, -6.0F, -4.0F, 3, 12, 8),
            ModelTransform.of(
                    -15.0F, -20.0F, 0.0F,
                    -15.0F * MathHelper.RADIANS_PER_DEGREE, 30.0F * MathHelper.RADIANS_PER_DEGREE, 0.0F )
            );

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(ShockerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float multiplier = entity.isAuraCharging() || entity.isAuraReleasing() ? 15.0F : 1.0F;

        this.base.pivotY = -5.0F + MathHelper.sin(animationProgress * 0.1F) * 4.0F;
        this.bottomFloater.pivotY = MathHelper.sin((animationProgress + 2.0F) * 0.1F) * 4.0F;
        this.rightFloater.pivotY = -20.0F + MathHelper.sin((animationProgress + 5.0F) * 0.1F) * 10.0F;
        this.leftFloater.pivotY = -32.0F + MathHelper.sin((animationProgress + 8.0F) * 0.1F) * 8.0F;
        this.rightFloater.pivotX = -15.0F - MathHelper.sin(animationProgress * multiplier * 0.1F) * multiplier * 0.25F;
        this.leftFloater.pivotX = 15.0F + MathHelper.sin((animationProgress - 0.5F) * multiplier * 0.1F) * multiplier * 0.25F;
        this.eye.pivotY = MathHelper.sin(animationProgress * 0.1F * multiplier) * 1.5F;
        this.eye.pivotX = MathHelper.cos(animationProgress * 0.1F * multiplier) * 1.5F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
