package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.JumpingZombieEntityModel;
import com.github.mim1q.minecells.client.renderer.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class JumpingZombieEntityRenderer extends MobEntityRenderer<JumpingZombieEntity, JumpingZombieEntityModel> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/jumping_zombie/jumping_zombie.png");
    private static final Identifier GLOW_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/jumping_zombie/jumping_zombie_glow.png");

    public JumpingZombieEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new JumpingZombieEntityModel(ctx.getPart(RendererRegistry.JUMPING_ZOMBIE_LAYER)), 0.35F);
        this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
    }

    @Override
    public Identifier getTexture(JumpingZombieEntity entity) {
        return TEXTURE;
    }
}