package com.github.mim1q.minecells.client.render.model.nonliving;

import com.github.mim1q.minecells.entity.nonliving.obelisk.ObeliskEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ObeliskEntityModel extends EntityModel<ObeliskEntity> {

  private final ModelPart root;
  private final ModelPart left;
  private final ModelPart center;
  private final ModelPart right;

  public ObeliskEntityModel(ModelPart root) {
    this.root = root;
    this.left = root.getChild("left");
    this.center = root.getChild("center");
    this.right = root.getChild("right");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData dRoot = modelData.getRoot();

    dRoot.addChild("center",
      ModelPartBuilder.create()
        .uv(96, 80).cuboid(-8.0F, -48.0F, -8.25F, 16.0F, 48.0F, 0.0F)
        .uv(8, 0).cuboid(0.0F, -48.0F, -8.0F, 8.0F, 16.0F, 16.0F)
        .uv(0, 0).cuboid(-8.0F, -32.0F, -8.0F, 16.0F, 32.0F, 16.0F),
      ModelTransform.pivot(0.0F, 24.0F, 0.0F)
    );

    dRoot.addChild("left",
      ModelPartBuilder.create()
        .uv(2, 5).cuboid(8.0F, -12.0F, -8.0F, 4.0F, 12.0F, 4.0F)
        .uv(48, 48).cuboid(8.0F, -32.0F, -4.0F, 8.0F, 32.0F, 8.0F)
        .uv(0, 47).cuboid(16.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F)
        .uv(0, 33).cuboid(8.0F, -12.0F, 4.0F, 4.0F, 12.0F, 4.0F),
      ModelTransform.pivot(0.0F, 24.0F, 0.0F)
    );

    dRoot.addChild("right",
      ModelPartBuilder.create()
        .uv(64, 0).cuboid(-16.0F, -24.0F, -4.0F, 8.0F, 24.0F, 8.0F)
        .uv(8, 28).cuboid(-12.0F, -16.0F, -8.0F, 4.0F, 16.0F, 4.0F)
        .uv(39, 11).cuboid(-20.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F)
        .uv(64, 30).cuboid(-8.0F, -40.0F, -4.0F, 8.0F, 8.0F, 8.0F)
        .uv(5, 21).cuboid(-12.0F, -16.0F, 4.0F, 4.0F, 16.0F, 4.0F),
      ModelTransform.pivot(0.0F, 24.0F, 0.0F)
    );

    return TexturedModelData.of(modelData, 128, 128);
  }

  @Override
  public void setAngles(ObeliskEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    entity.bury.update(animationProgress);
    float bury = entity.bury.getValue();
    this.root.pivotY = 0.0F + bury;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }
}
