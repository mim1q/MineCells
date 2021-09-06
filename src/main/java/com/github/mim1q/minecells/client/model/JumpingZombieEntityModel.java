package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class JumpingZombieEntityModel extends AnimatedGeoModel<JumpingZombieEntity>
{
    @Override
    public Identifier getModelLocation(JumpingZombieEntity object)
    {
        return new Identifier(MineCells.MOD_ID, "geo/entity/jumping_zombie.geo.json");
    }

    @Override
    public Identifier getTextureLocation(JumpingZombieEntity object)
    {
        return new Identifier(MineCells.MOD_ID, "textures/entity/jumping_zombie.png");
    }

    @Override
    public Identifier getAnimationFileLocation(JumpingZombieEntity object)
    {
        return new Identifier(MineCells.MOD_ID, "animations/entity/jumping_zombie.animation.json");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setLivingAnimations(JumpingZombieEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("Head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationY((extraData.netHeadYaw) * ((float) Math.PI / 340F));
        }
    }
}
