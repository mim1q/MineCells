package com.github.mim1q.minecells.client.render.model.nonliving;

import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class TentacleWeaponEntityModel extends EntityModel<TentacleWeaponEntity> {

  private final ModelPart main;
  private float length = 0.0F;

  public TentacleWeaponEntityModel(ModelPart root) {
    this.main = root;
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    modelPartData.addChild("main",
      ModelPartBuilder.create()
        .uv(0, 0)
        .cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 16.0F),
      ModelTransform.NONE
    );

    return TexturedModelData.of(modelData, 64, 64);
  }

  @Override
  public void setAngles(TentacleWeaponEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    float tickDelta = MinecraftClient.getInstance().getTickDelta();
    this.main.yaw = MathUtils.radians(entity.getYaw(tickDelta));
    this.main.pitch = MathUtils.radians(-entity.getPitch(tickDelta));
    this.length = entity.getLength(tickDelta);
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    matrices.push();
    this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    matrices.pop();
  }
}
