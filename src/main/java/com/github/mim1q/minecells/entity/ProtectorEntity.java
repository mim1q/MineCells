package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.network.PacketIdentifiers;
import com.github.mim1q.minecells.registry.StatusEffectRegistry;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class ProtectorEntity extends MineCellsEntity {

    protected int stateTicks = 0;

    public ProtectorEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        String state = this.getAttackState();
        if (!this.world.isClient()) {
            if (stateTicks > 40 && state.equals("none")) {
                this.stateTicks = 0;
                this.setAttackState("protect");
            }
            if (state.equals("protect")) {
                List<Entity> entities = this.world.getOtherEntities(this, Box.of(this.getPos(), 15.0D, 15.0D, 15.0D), ProtectorEntity::canProtect);
                for (Entity e : entities) {
                    StatusEffectInstance effect = new StatusEffectInstance(StatusEffectRegistry.PROTECTED, 60 - stateTicks, 5, false, false);
                    ((LivingEntity)e).addStatusEffect(effect);
                    for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld)this.world, this.getPos(), 30.0F)) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeDouble(this.getX());
                        buf.writeDouble(this.getY() + this.getHeight() / 2.0D);
                        buf.writeDouble(this.getZ());
                        buf.writeDouble(e.getX());
                        buf.writeDouble(e.getY() + e.getHeight() / 2.0D);
                        buf.writeDouble(e.getZ());
                        ServerPlayNetworking.send(player, PacketIdentifiers.CONNECT, buf);
                    }
                }
                if (stateTicks > 60) {
                    this.stateTicks = 0;
                    this.setAttackState("none");
                }
            }
        }

        if (state.equals("protect")) {
            if (this.world.isClient()) {
                ParticleHelper.addAura((ClientWorld)this.world, this.getPos().add(0.0D, 1.5D, 0.0D), ParticleTypes.ENCHANTED_HIT, 1, 0.1D, 0.5D);
            }
        }

        stateTicks++;
        super.tick();
        this.setPosition(this.prevX, this.getY(), this.prevZ);
    }

    protected static boolean canProtect(Entity e) {
        if (e instanceof KamikazeEntity) { return false; }
        if (e instanceof ProtectorEntity) { return false; }

        return e instanceof HostileEntity;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public static DefaultAttributeContainer.Builder createProtectorAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0F)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0F)
            .add(EntityAttributes.GENERIC_ARMOR, 5.0F);
    }
}
