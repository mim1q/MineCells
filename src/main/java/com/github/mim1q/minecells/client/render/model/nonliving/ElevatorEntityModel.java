package com.github.mim1q.minecells.client.render.model.nonliving;

import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ElevatorEntityModel extends EntityModel<ElevatorEntity> {

    private final ModelPart root;

    public ElevatorEntityModel(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("root",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-16.0F, 0.0F, -16.0F, 32, 3, 32)
                        .cuboid(-16.0F, 5.0F, -16.0F, 32, 3, 32)
                        .cuboid(-18.0F, -1.0F, -2.0F, 4, 10, 4)
                        .cuboid(14.0F, -1.0F, -2.0F, 4, 10, 4)
                        .uv(0, 5)
                        .cuboid(-15.0F, 3.0F, -15.0F, 30, 2, 30),
                ModelTransform.NONE);

        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    public void setAngles(ElevatorEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
