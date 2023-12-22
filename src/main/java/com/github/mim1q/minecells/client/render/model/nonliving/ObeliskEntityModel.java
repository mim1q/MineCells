package com.github.mim1q.minecells.client.render.model.nonliving;

import com.github.mim1q.minecells.entity.nonliving.obelisk.ObeliskEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ObeliskEntityModel extends EntityModel<ObeliskEntity> {

  private final ModelPart main;
  private final ModelPart glowMain;

  public ObeliskEntityModel(ModelPart root) {
    this.main = root.getChild("main");
    this.glowMain = root.getChild("glowMain");
  }

  public static TexturedModelData getTexturedModelData() {
    var modelData = new ModelData();
    var modelPartData = modelData.getRoot();
    var main = modelPartData.addChild("main", ModelPartBuilder.create().uv(80, 45).cuboid(14.0F, -40.0F, -0.5F, 6.0F, 40.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.0F, 24.0F, 0.0F));
    main.addChild("cube_r1", ModelPartBuilder.create().uv(0, 48).cuboid(0.0F, -12.0F, -5.0F, 10.0F, 12.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
    main.addChild("cube_r2", ModelPartBuilder.create().uv(38, 38).cuboid(0.0F, -24.0F, -4.0F, 11.0F, 24.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, -0.1745F));
    main.addChild("cube_r3", ModelPartBuilder.create().uv(48, 0).cuboid(0.0F, -15.0F, 1.0F, 10.0F, 15.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(12.0F, 0.0F, -5.0F, -0.1745F, 0.0F, 0.1745F));
    main.addChild("cube_r4", ModelPartBuilder.create().uv(1, 0).cuboid(1.0F, -36.0F, -12.0F, 11.0F, 36.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F));
    var glowMain = modelPartData.addChild("glowMain", ModelPartBuilder.create().uv(104, 45).cuboid(14.0F, -40.0F, -0.5F, 6.0F, 40.0F, 6.0F, new Dilation(0.01F)), ModelTransform.pivot(-7.0F, 24.0F, 0.0F));
    glowMain.addChild("cube_r5", ModelPartBuilder.create().uv(1, 80).cuboid(1.0F, -36.0F, -12.0F, 11.0F, 36.0F, 12.0F, new Dilation(0.01F)), ModelTransform.of(2.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F));
    glowMain.addChild("cube_r6", ModelPartBuilder.create().uv(92, 25).cuboid(0.0F, -12.0F, -5.0F, 10.0F, 12.0F, 8.0F, new Dilation(0.01F)), ModelTransform.of(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
    glowMain.addChild("cube_r7", ModelPartBuilder.create().uv(48, 94).cuboid(0.0F, -24.0F, -4.0F, 11.0F, 24.0F, 10.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, -0.1745F));
    glowMain.addChild("cube_r8", ModelPartBuilder.create().uv(88, 0).cuboid(0.0F, -15.0F, 1.0F, 10.0F, 15.0F, 10.0F, new Dilation(0.01F)), ModelTransform.of(12.0F, 0.0F, -5.0F, -0.1745F, 0.0F, 0.1745F));
    return TexturedModelData.of(modelData, 128, 128);
  }

  @Override
  public void setAngles(ObeliskEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    var bury = entity.bury.update(animationProgress);
    this.main.pivotY = 24.0F + bury;
    this.glowMain.pivotY = 24.0F + bury;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }

  public void renderGlow(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    if (alpha <= 0) return;
    this.glowMain.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }
}
