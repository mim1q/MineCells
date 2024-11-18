package com.github.mim1q.minecells.client.render.conjunctivius;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.conjunctivius.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class ConjunctiviusChainRenderer extends FeatureRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {
  private final static Identifier MODEL_ID = MineCells.createId("misc/conjunctivius/chain");
  private final static Identifier DASH_MODEL_ID = MineCells.createId("misc/conjunctivius/dash_chain");

  private final BakedModel model;
  private final BakedModel dashModel;

  public ConjunctiviusChainRenderer(
    FeatureRendererContext<ConjunctiviusEntity, ConjunctiviusEntityModel> context,
     BakedModelManager modelManager
  ) {
    super(context);
    model = modelManager.getModel(MODEL_ID);
    dashModel = modelManager.getModel(DASH_MODEL_ID);
  }

  @Override
  public void render(
    MatrixStack matrices,
    VertexConsumerProvider vertexConsumers,
    int light,
    ConjunctiviusEntity entity,
    float limbAngle,
    float limbDistance,
    float tickDelta,
    float animationProgress,
    float headYaw,
    float headPitch
  ) {
    if (entity.isForDisplay()) {
      return;
    }

    var startPos = entity.getLerpedPos(tickDelta);

    var vertices = vertexConsumers.getBuffer(RenderLayer.getCutout());
    var stage = entity.getStage();
    if (stage < 2) {
      renderChain(
        entity, matrices, vertices, startPos,
        Vec3d.ofBottomCenter(entity.getRightAnchor()),
        entity.getYaw(), new Vector3f(2.2F, 0.85F, 0.0F), false
      );
    }
    if (stage < 4) {
      renderChain(
        entity, matrices, vertices, startPos,
        Vec3d.ofBottomCenter(entity.getLeftAnchor()),
        entity.getYaw(), new Vector3f(-2.2F, 0.85F, 0.0F), false
      );
    }
    if (stage < 6) {
      renderChain(
        entity, matrices, vertices, startPos,
        Vec3d.ofBottomCenter(entity.getTopAnchor().up()),
        entity.getYaw(), new Vector3f(0.0F, -1.75F, 0.0F), false
      );
    }

    var dashVertices = vertexConsumers.getBuffer(RenderLayer.getTranslucent());
    if (entity.getDashState() == TimedActionGoal.State.CHARGE) {
      var target = entity.getDashTarget();
      if (!target.equals(Vec3d.ZERO)) {
        renderChain(
          entity, matrices, dashVertices, startPos,
          target,
          entity.getYaw(), new Vector3f(0.0F, 0.0F, 0.0F), true
        );
      }
    }
  }

  protected void renderChain(
    ConjunctiviusEntity entity,
    MatrixStack matrices,
    VertexConsumer vertices,
    Vec3d startPos,
    Vec3d targetPos,
    float headYaw,
    Vector3f offset,
    boolean isDash
  ) {
    var modelToUse = isDash ? dashModel : model;

    matrices.push();
    matrices.scale(0.75F, 0.75F, 0.75F);
    startPos = startPos.add(0.0D, -offset.y * 2.0D + 3.0D, 0.01 - offset.z * 1.5D);
    var direction = targetPos.subtract(startPos);
    var normDir = direction.normalize().multiply(0.75);
    direction = direction.rotateY(MathUtils.radians(headYaw));
    direction = direction.add(-offset.x * 1.5D, 0.0D, 0.0);

    var rx = (float) -Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
    var ry = (float) -Math.atan2(direction.x, direction.z);
    var posRotScale = MathUtils.PosRotScale.ofRadians(
      offset,
      new Vector3f(rx, ry, 0.0F),
      new Vector3f(1.0F, 1.0F, 1.0F)
    );
    posRotScale.apply(matrices);
    var count = (int) (direction.length());
    matrices.translate(-0.5, -0.5, -1.25);

    var lightPos = startPos.add(-offset.x * 1.5, 0.0, offset.z);
    var light = 0xF000F0;
    for (int i = 0; i < count; i++) {
      if (!isDash) {
        var lightLevel = entity.getWorld().getLightLevel(BlockPos.ofFloored(lightPos));
        light = LightmapTextureManager.pack(lightLevel, lightLevel);
        lightPos = lightPos.add(normDir);
      }
      RenderUtils.renderBakedModel(modelToUse, entity.getRandom(), light, matrices, vertices);
      matrices.translate(0.0F, 0.0F, -1.0F);
    }
    matrices.pop();
  }
}
