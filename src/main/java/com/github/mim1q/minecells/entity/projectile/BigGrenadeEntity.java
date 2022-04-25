package com.github.mim1q.minecells.entity.projectile;

import com.github.mim1q.minecells.registry.EntityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class BigGrenadeEntity extends GrenadeEntity {
    public BigGrenadeEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public void explode() {
        this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 3.0F, Explosion.DestructionType.BREAK);
        for (int i = 0; i < 3; i++) {
            GrenadeEntity.spawn(new GrenadeEntity(EntityRegistry.GRENADE, this.world), getX(), getY(), getZ());
        }
    }
}
