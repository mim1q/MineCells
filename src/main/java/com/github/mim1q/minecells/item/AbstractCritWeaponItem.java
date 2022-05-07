package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.network.PacketHandler;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.Vanishable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract  class AbstractCritWeaponItem extends ToolItem implements Vanishable {
    protected final float attackDamage;
    protected final float critAttackDamage;

    protected final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public AbstractCritWeaponItem(ToolMaterial toolMaterial, float attackDamage, float critAttackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, settings);
        this.attackDamage = attackDamage;
        this.critAttackDamage = critAttackDamage;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public abstract boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker);

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.canCrit(stack, target, attacker)) {
            attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundRegistry.CRIT, SoundCategory.PLAYERS, 0.5F, 1.0F);
            for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld)target.world, target.getPos(), 30.0D)) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeDouble(target.getX());
                buf.writeDouble(target.getY() + 1.5D);
                buf.writeDouble(target.getZ());
                ServerPlayNetworking.send(player, PacketHandler.CRIT, buf);
            }
            target.damage(DamageSource.mob(attacker), this.attackDamage + this.critAttackDamage);
        }
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return super.postHit(stack, target, attacker);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return super.postMine(stack, world, state, pos, miner);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }
}
