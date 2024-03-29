package com.github.mim1q.minecells.client.render.model.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ConjunctiviusProjectileEntityModel extends EntityModel<ConjunctiviusProjectileEntity> {

  private final ModelPart root;
  private final ModelPart main;

  public ConjunctiviusProjectileEntityModel(ModelPart root) {
    this.root = root;
    this.main = root.getChild("main");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    modelPartData.addChild("main",
      ModelPartBuilder.create()
        .uv(0, 0)
        .cuboid(-2.0F, -2.0F, -2.0F, 4, 4, 12),
      ModelTransform.NONE
    );

    return TexturedModelData.of(modelData, 32, 32);
  }

  @Override
  public void setAngles(ConjunctiviusProjectileEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    this.root.pitch = MathUtils.radians(-180.0F + entity.getPitch());
    this.root.yaw = MathUtils.radians(-entity.getYaw());
    this.main.roll = ((int)(-entity.age * 0.5F) % 4) * MathHelper.HALF_PI;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    matrices.push();
    matrices.translate(0.0F, 0.25F, 0.0F);
    this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    matrices.pop();
  }
}
