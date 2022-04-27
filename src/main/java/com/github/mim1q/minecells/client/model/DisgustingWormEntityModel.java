package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.DisgustingWormEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class DisgustingWormEntityModel extends AnimatedGeoModel<DisgustingWormEntity> {
    @Override
    public Identifier getModelLocation(DisgustingWormEntity object) {
        return new Identifier(MineCells.MOD_ID, "geo/entity/disgusting_worm.geo.json");
    }

    @Override
    public Identifier getTextureLocation(DisgustingWormEntity object) {
        return new Identifier(MineCells.MOD_ID, "textures/entity/disgusting_worm/disgusting_worm.png");
    }

    @Override
    public Identifier getAnimationFileLocation(DisgustingWormEntity object) {
        return new Identifier(MineCells.MOD_ID, "animations/entity/disgusting_worm.animation.json");
    }

    @Override
    public void setMolangQueries(IAnimatable animatable, double currentTick) {
        DisgustingWormEntity entity = (DisgustingWormEntity)animatable;
        super.setMolangQueries(animatable, currentTick);
        MolangParser parser = GeckoLibCache.getInstance().parser;
        parser.setValue("amount", 0.5D);
        parser.setValue("speed", entity.isAttacking() ? 3.0D : 1.5D);
    }
}
