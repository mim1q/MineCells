package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class GrenadeEntityRenderer extends AbstractGrenadeEntityRenderer<GrenadeEntity> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/grenades/grenade.png");
    private static final GrenadeEntityModel MODEL = new GrenadeEntityModel(GrenadeEntityModel.getTexturedModelData().createModel());

    public GrenadeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, TEXTURE, TEXTURE, MODEL);
    }
}
