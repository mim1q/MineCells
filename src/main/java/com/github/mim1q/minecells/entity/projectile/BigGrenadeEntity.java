package com.github.mim1q.minecells.entity.projectile;

import com.github.mim1q.minecells.registry.EntityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class BigGrenadeEntity extends GrenadeEntity {
    public BigGrenadeEntity(EntityType<BigGrenadeEntity> type, World world) {
        super(type, world);
    }

    @Override
    public int getMaxFuse() {
        return 30;
    }

    public void explode() {
        this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 3.0F, Explosion.DestructionType.NONE);
        for (int i = 0; i < 3; i++) {
            Vec3d velocity = new Vec3d(this.random.nextDouble() - 0.5D, this.random.nextDouble(), this.random.nextDouble() - 0.5D).multiply(0.7D);

            GrenadeEntity grenade = new GrenadeEntity(EntityRegistry.GRENADE, this.world);
            grenade.setPosition(this.getPos());
            grenade.shoot(velocity);

            this.world.spawnEntity(grenade);
        }
    }
}
