package com.github.mim1q.minecells.client.model.projectile;

import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class GrenadeEntityModel extends EntityModel<GrenadeEntity> {

    private final ModelPart body;
    private static final float SIZE = 8.0F;

    public GrenadeEntityModel(ModelPart root) {
        this.body = root.getChild("body");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-SIZE / 2.0F, 0.0F, -SIZE / 2.0F, SIZE, SIZE, SIZE),
                ModelTransform.NONE
        );
        return TexturedModelData.of(modelData, (int)SIZE * 4, (int)SIZE * 2);
    }

    @Override
    public void setAngles(GrenadeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) { }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        ImmutableList.of(this.body).forEach((modelRenderer) -> modelRenderer.render(matrices, vertices, light, overlay, red, green, blue, alpha));
    }
}
