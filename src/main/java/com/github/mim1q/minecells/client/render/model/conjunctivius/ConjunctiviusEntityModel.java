package com.github.mim1q.minecells.client.render.model.conjunctivius;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ConjunctiviusEntityModel extends EntityModel<ConjunctiviusEntity> {

  private final ModelPart main;

  public ConjunctiviusEntityModel(ModelPart root) {
    super(RenderLayer::getEntityCutout);
    this.main = root.getChild("main");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    ModelPartData dMain = modelPartData.addChild("main",
      ModelPartBuilder.create()
        .uv(96, 0)
        .cuboid(-12.0F, -4.0F, -12.0F, 24, 4, 24) // Teeth
        .uv(128, 28)
        .cuboid(-8.0F, 0.0F, -8.0F, 16, 4, 16) // Mouth
        .uv(0, 0)
        .cuboid(-16.0F, -36.0F, -16.0F, 32, 32, 32) // Main Body
        .uv(138, 94)
        .cuboid(-12.0F, -31.0F, -15.5F, 24, 22, 0) // Eye Background
        .uv(92, 64)
        .cuboid(-12.0F, -42.0F, -12.0F, 24, 6, 24) // Head Top
        .uv(46, 70)
        .cuboid(-10.0F, -48.0F, 0.0F, 20, 6, 0) // Pulled Head Skin
        .uv(102, 98)
        .cuboid(16.0F, -22.0F, -12.0F, 6, 16, 24) // Lower Left Side
        .uv(60, 122)
        .cuboid(16.0F, -30.0F, -10.0F, 3, 8, 20) // Upper Left Side
        .uv(0, 110)
        .cuboid(-22.0F, -22.0F, -12.0F, 6, 16, 24) // Lower Right Side
        .uv(86, 138)
        .cuboid(-19.0F, -30.0F, -10.0F, 3, 8, 20) // Upper Right Side
        .uv(0, 64)
        .cuboid(22.0F, -21.0F, 0.0F, 8, 14, 0) // Left Spike Flat
        .uv(16, 64)
        .cuboid(22.0F, -16.0F, -2.0F, 4, 4, 4) // Left Spike Cube
        .uv(0, 64).mirrored()
        .cuboid(-30.0F, -21.0F, 0.0F, 8, 14, 0) // Right Spike Flat
        .uv(16, 64).mirrored()
        .cuboid(-26.0F, -16.0F, -2.0F, 4, 4, 4), // Right Spike Cube
      ModelTransform.pivot(0.0F, 24.0F, 0.0F)
    );

    dMain.addChild("crown_front",
      ModelPartBuilder.create()
        .uv(36, 122)
        .cuboid(-9.0F, -4.0F, -4.0F, 18, 4, 4),
      ModelTransform.of(0.0F, -36.0F, -12.0F, MathUtils.radians(20.0F), 0.0F, 0.0F)
    );

    dMain.addChild("crown_back",
      ModelPartBuilder.create()
        .uv(128, 48)
        .cuboid(-9.0F, -4.0F, 0.0F, 18, 4, 4),
      ModelTransform.of(0.0F, -36.0F, 12.0F, MathUtils.radians(-20.0F), 0.0F, 0.0F)
    );

    dMain.addChild("crown_left",
      ModelPartBuilder.create()
        .uv(0, 64)
        .cuboid(-6.0F, 0.0F, -17.0F, 6, 12, 34),
      ModelTransform.of(16.0F, -41.0F, 0.0F, 0.0F, 0.0F, MathUtils.radians(15.0F))
    );

    dMain.addChild("crown_right",
      ModelPartBuilder.create()
        .uv(0, 64).mirrored()
        .cuboid(0.0F, 0.0F, -17.0F, 6, 12, 34),
      ModelTransform.of(-16.0F, -41.0F, 0.0F, 0.0F, 0.0F, MathUtils.radians(-15.0F))
    );

    return TexturedModelData.of(modelData, 256, 256);
  }

  @Override
  public void setAngles(ConjunctiviusEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }
}
