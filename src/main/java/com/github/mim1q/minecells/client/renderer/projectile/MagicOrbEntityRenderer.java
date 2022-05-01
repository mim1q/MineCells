package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.entity.projectile.MagicOrbEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class MagicOrbEntityRenderer extends EntityRenderer<MagicOrbEntity> {

    public MagicOrbEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(MagicOrbEntity entity) {
        return null;
    }

}
