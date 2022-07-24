package com.github.mim1q.minecells.client.render.conjunctivius;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.conjunctivius.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ConjunctiviusEyeRenderer extends FeatureRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel>  {

  private final static Identifier[] TEXTURES = {
    MineCells.createId("textures/entity/conjunctivius/eye_pink.png"),
    MineCells.createId("textures/entity/conjunctivius/eye_yellow.png"),
    MineCells.createId("textures/entity/conjunctivius/eye_green.png"),
    MineCells.createId("textures/entity/conjunctivius/eye_blue.png")
  };

  private final ConjunctiviusEyeModel model;

  public ConjunctiviusEyeRenderer(FeatureRendererContext<ConjunctiviusEntity, ConjunctiviusEntityModel> context, ModelPart eyeRoot) {
    super(context);
    this.model = new ConjunctiviusEyeModel(eyeRoot);
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ConjunctiviusEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    matrices.push();
    matrices.translate(0.0F, 0.2F, -15.75F / 16.0F);
    this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
    RenderLayer renderLayer = RenderLayer.getEntityShadow(this.getTexture(entity));
    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
    this.model.render(matrices, vertexConsumer, 0xFF, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  public Identifier getTexture(ConjunctiviusEntity entity) {
    return TEXTURES[entity.getEyeState().index];
  }

  public enum EyeState {
    PINK(0),
    YELLOW(1),
    GREEN(2),
    BLUE(3);

    final int index;

    EyeState(int index) {
      this.index = index;
    }
  }

  public static class ConjunctiviusEyeModel extends EntityModel<ConjunctiviusEntity> {

    private final ModelPart eye;
    private final ModelPart highlight;

    public ConjunctiviusEyeModel(ModelPart root) {
      this.eye = root.getChild("eye");
      this.highlight = root.getChild("highlight");
    }

    public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();

      modelPartData.addChild("eye",
        ModelPartBuilder.create()
          .uv(0, 0)
          .cuboid(-5.5F, -5.5F, 0.0F, 11, 11, 0, new Dilation(0.01F)),
        ModelTransform.NONE
      );

      modelPartData.addChild("highlight",
        ModelPartBuilder.create()
          .uv(0, 11)
          .cuboid(0.0F, 0.0F, 0.0F, 5, 4, 0, new Dilation(0.01F)),
        ModelTransform.pivot(1.0F, -5.0F, -0.25F)
      );

      return TexturedModelData.of(modelData, 32, 16);
    }

    @Override
    public void setAngles(ConjunctiviusEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
      Entity player = MinecraftClient.getInstance().getCameraEntity();
      if (player != null) {
        Vec3d playerPos = player.getPos().add(0.0D, 1.5D, 0.0D);
        Vec3d entityPos = entity.getPos().add(0.0D, 2.5D, 0.0D);
        Vec3d diff = playerPos.subtract(entityPos);
        float rotation = entity.bodyYaw;
        Vec3d rotatedDiff = MathUtils.vectorRotateY(diff, rotation * MathHelper.RADIANS_PER_DEGREE + MathHelper.HALF_PI);

        float xOffset = (float) -rotatedDiff.x;
        float yOffset = (float) -rotatedDiff.y;
        float distance = 1.0F - ((float) rotatedDiff.z - 2.5F) / 30.0F;
        distance = MathHelper.clamp(distance, 0.25F, 1.0F);

        xOffset = MathHelper.clamp(xOffset * 0.75F * distance, -7.5F, 7.5F);
        yOffset = MathHelper.clamp(yOffset * distance, -5.0F, 5.0F);

        this.eye.pivotX = xOffset;
        this.eye.pivotY = yOffset;
        this.highlight.pivotZ = -0.25F;
      }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      matrices.push();
      this.eye.render(matrices, vertices, light, overlay, red, green, blue, alpha);
      this.highlight.render(matrices, vertices, light, overlay, red, green, blue, alpha);
      matrices.pop();
    }
  }
}
