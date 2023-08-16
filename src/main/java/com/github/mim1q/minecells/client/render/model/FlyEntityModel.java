package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.entity.FlyEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class FlyEntityModel<T extends FlyEntity> extends EntityModel<T> {
  private final ModelPart root;
  private final ModelPart body;
  private final ModelPart[] leftWing = new ModelPart[2];
  private final ModelPart[] rightWing = new ModelPart[2];
  private final ModelPart leftTooth;
  private final ModelPart rightTooth;
  private final ModelPart biteTooth;

  public FlyEntityModel(ModelPart root) {
    super(RenderLayer::getEntityCutout);
    this.root = root.getChild("root");
    this.body = this.root.getChild("body");
    this.leftWing[0] = this.body.getChild("left_wing_0");
    this.leftWing[1] = this.leftWing[0].getChild("left_wing_1");
    this.rightWing[0] = this.body.getChild("right_wing_0");
    this.rightWing[1] = this.rightWing[0].getChild("right_wing_1");
    this.leftTooth = this.body.getChild("left_tooth");
    this.rightTooth = this.body.getChild("right_tooth");
    this.biteTooth = this.body.getChild("bite_tooth");
  }

  @Override
  public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    leftWing[0].roll = (float) Math.sin(animationProgress * 0.5F) * 0.75F;
    leftWing[1].roll = (float) Math.sin(animationProgress * 0.5F - 0.8F);
    body.pivotY = (float) Math.sin(animationProgress * 0.5F + 1.5F) * 2.0F;

    entity.bite.update(animationProgress);
    var bite = entity.bite.getValue();

    biteTooth.yScale = bite;
    biteTooth.zScale = bite;
    biteTooth.pitch = MathUtils.radians((1 - bite) * 160F);
    rightTooth.roll = MathUtils.radians(bite * 20F);
    leftTooth.roll = -rightTooth.roll;
    leftWing[0].roll = MathUtils.lerp(leftWing[0].roll, MathUtils.radians(-60F), bite * 0.75F);
    leftWing[1].roll = MathUtils.lerp(leftWing[0].roll, MathUtils.radians(-30F), bite * 0.25F);
    body.pivotY = MathUtils.lerp(body.pivotY, (float) Math.sin(animationProgress * 2F), bite * 0.75F);
    body.pitch = bite * -0.5F;

    rightWing[0].roll = -leftWing[0].roll;
    rightWing[1].roll = -leftWing[1].roll;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
  }

  public static TexturedModelData getTexturedModelData() {
    var modelData = new ModelData();
    var modelPartData = modelData.getRoot();
    var root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 20.0F, 0.0F));
    var body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
    body.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 7.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.8727F, 0.0F, 0.0F));
    body.addChild("bite_tooth", ModelPartBuilder.create().uv(0, 32).cuboid(0.0F, -15.0F, -12.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -5.0F));
    body.addChild("left_tooth", ModelPartBuilder.create()
      .uv(0, 4).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
      .uv(4, 4).cuboid(-1.0F, 0.0F, -4.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 1.0F, -5.0F, 1.4835F, 0.0F, 0.0F));
    body.addChild("right_tooth", ModelPartBuilder.create()
      .uv(0, 0).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
      .uv(4, 0).cuboid(-1.0F, 0.0F, -4.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.25F, 1.0F, -5.0F, 1.4835F, 0.0F, 0.0F));
    var leftWing0 = body.addChild("left_wing_0", ModelPartBuilder.create().uv(21, 0).cuboid(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -2.0F, 1.0F));
    leftWing0.addChild("left_wing_1", ModelPartBuilder.create().uv(0, 24).cuboid(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 0.0F));
    var rightWing0 = body.addChild("right_wing_0", ModelPartBuilder.create().uv(21, 0).mirrored().cuboid(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-4.0F, -2.0F, 1.0F));
    rightWing0.addChild("right_wing_1", ModelPartBuilder.create().uv(0, 24).mirrored().cuboid(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-8.0F, 0.0F, 0.0F));
    return TexturedModelData.of(modelData, 64, 64);
  }
}
