package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.nonliving.TentacleWeaponEntityModel;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class TentacleWeaponEntityRenderer extends EntityRenderer<TentacleWeaponEntity> {
  private static final Identifier TEXTURE = MineCells.createId("textures/entity/tentacle_weapon.png");
  private final TentacleWeaponEntityModel model;

  public TentacleWeaponEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
    this.model = new TentacleWeaponEntityModel(ctx.getPart(MineCellsRenderers.TENTACLE_WEAPON_LAYER));
  }

  @Override
  public void render(TentacleWeaponEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    if (entity.age < 2) {
      return;
    }
    matrices.push();
    matrices.scale(-1.0F, -1.0F, 1.0F);
    float length = entity.getLength(tickDelta);
    Vec3f normalized = new Vec3f(entity.getEndPos(Math.max(1.0F, length)).subtract(entity.getCameraPosVec(tickDelta)).normalize());
    float xRot = (float) Math.asin(-normalized.getY());
    float yRot = (float) Math.atan2(normalized.getX(), normalized.getZ());
    matrices.multiply(new Quaternion(Vec3f.POSITIVE_Y, -yRot, false));
    matrices.multiply(new Quaternion(Vec3f.POSITIVE_X, -xRot, false));
    renderTentacle(matrices, vertexConsumers, light, length);
    matrices.pop();

    super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
  }

  public void renderTentacle(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float length) {
    matrices.push();
    matrices.scale(1.0F, 1.0F, length * 10.0F);
    this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(TEXTURE)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  @Override
  public Identifier getTexture(TentacleWeaponEntity entity) {
    return TEXTURE;
  }
}
