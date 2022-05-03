package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.client.model.JumpingZombieEntityModel;
import com.github.mim1q.minecells.client.renderer.layer.JumpingZombieGlowLayer;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JumpingZombieEntityRenderer extends EntityRenderer<JumpingZombieEntity> {

    protected JumpingZombieEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(JumpingZombieEntity entity) {
        return null;
    }
}