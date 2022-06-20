package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.KamikazeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class KamikazeEntityModel extends EntityModel<KamikazeEntity> {

    private final ModelPart root;
    private final ModelPart bulb;
    private final ModelPart lowerLeftWing;
    private final ModelPart upperLeftWing;
    private final ModelPart lowerRightWing;
    private final ModelPart upperRightWing;

    private int fuse = -1;

    public KamikazeEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.bulb = root.getChild("bulb");
        this.lowerLeftWing = this.root.getChild("lower_left_wing");
        this.upperLeftWing = this.lowerLeftWing.getChild("upper_left_wing");
        this.lowerRightWing = this.root.getChild("lower_right_wing");
        this.upperRightWing = this.lowerRightWing.getChild("upper_right_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData dRoot = modelPartData.addChild("root",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-1.5F, -1.0F, 0.0F, 3, 1, 0, new Dilation(0.01F))
                .uv(12, 18)
                .cuboid(-1.5F, -5.0F, -1.0F, 3, 4, 2),
            ModelTransform.pivot(0.0F, 18.0F, 0.0F));

        modelPartData.addChild("bulb",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-3.0F, 0.0F, -3.0F, 6, 6, 6),
            ModelTransform.pivot(0.0F, 18.0F, 0.0F));

        ModelPartData dLowerLeftWing = dRoot.addChild("lower_left_wing",
            ModelPartBuilder.create()
                .uv(18, 0)
                .cuboid(0.0F, -3.0F, 0.0F, 6, 6, 0, new Dilation(0.01F)),
            ModelTransform.of(1.0F, -3.5F, 0.0F, 20.0F * RADIANS_PER_DEGREE, 0.0F, 15.0F * RADIANS_PER_DEGREE));

        dLowerLeftWing.addChild("upper_left_wing",
            ModelPartBuilder.create()
                .uv(0, 18)
                .cuboid(-1.0F, -6.0F, 0.0F, 6, 6, 0, new Dilation(0.01F)),
            ModelTransform.pivot(1.0F, -2.0F, 0.0F));

        ModelPartData dLowerRightWing = dRoot.addChild("lower_right_wing",
            ModelPartBuilder.create()
                .uv(12, 12)
                .cuboid(-6.0F, -3.0F, 0.0F, 6, 6, 0, new Dilation(0.01F)),
            ModelTransform.of(-1.0F, -3.5F, 0.0F, 20.0F * RADIANS_PER_DEGREE, 0.0F, -15.0F * RADIANS_PER_DEGREE));

        dLowerRightWing.addChild("upper_right_wing",
            ModelPartBuilder.create()
                .uv(0, 12)
                .cuboid(-5.0F, -6.0F, 0.0F, 6, 6, 0, new Dilation(0.01F)),
            ModelTransform.pivot(-1.0F, -2.0F, 0.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(KamikazeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        if (entity.isSleeping()) {
            this.root.pitch = -MathHelper.PI;
            this.bulb.pitch = -MathHelper.PI;

            this.lowerLeftWing.yaw = 45.0F * RADIANS_PER_DEGREE;
            this.upperLeftWing.pitch = 120.0F * RADIANS_PER_DEGREE;

            entity.animationTimestamp = animationProgress;
        } else {
            this.lowerLeftWing.yaw = MathHelper.sin(animationProgress * 0.8F) * RADIANS_PER_DEGREE * 45.0F;
            this.upperLeftWing.pitch = -MathHelper.sin(animationProgress * 0.8F + 1.5F) * RADIANS_PER_DEGREE * 45.0F;
            this.upperLeftWing.pitch += 45.0F * RADIANS_PER_DEGREE;

            float animationTime = animationProgress - entity.animationTimestamp;
            this.root.pitch = MathHelper.clampedLerp(-MathHelper.PI, 0.0F, animationTime / 10.0F);
            this.bulb.pitch = this.root.pitch;
        }

        this.lowerRightWing.yaw = -this.lowerLeftWing.yaw;
        this.upperRightWing.pitch = this.upperLeftWing.pitch;

        this.fuse = entity.getFuse();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);

        matrices.push();
        if (this.fuse < 30 && this.fuse >= 0 && this.fuse / 2 % 3 == 0 ) {
            overlay = OverlayTexture.packUv(OverlayTexture.getU(1.0F), 10);
        }
        this.bulb.render(matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }
}
