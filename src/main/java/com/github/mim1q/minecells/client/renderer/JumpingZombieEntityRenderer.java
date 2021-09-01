package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.client.model.JumpingZombieEntityModel;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JumpingZombieEntityRenderer extends GeoEntityRenderer<JumpingZombieEntity> {
    public JumpingZombieEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new JumpingZombieEntityModel());
        this.shadowRadius = 0.25f;
    }
}