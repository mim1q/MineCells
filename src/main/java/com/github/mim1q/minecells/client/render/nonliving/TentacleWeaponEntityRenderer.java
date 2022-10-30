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
    this.model.setAngles(entity, 0, 0, 0, 0, 0);
    this.model.render(
      matrices,
      vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity))),
      light,
      OverlayTexture.DEFAULT_UV,
      1,
      1,
      1,
      1
    );
    matrices.pop();

    super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
  }

  @Override
  public Identifier getTexture(TentacleWeaponEntity entity) {
    return TEXTURE;
  }
}
