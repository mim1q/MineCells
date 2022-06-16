package com.github.mim1q.minecells.mixin;

import com.github.mim1q.minecells.accessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {

    @Accessor("dimensions")
    public abstract void setDimensions(EntityDimensions dimensions);
}
