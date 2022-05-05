package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.network.PacketHandler;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssassinsDaggerItem extends ToolItem {
    public AssassinsDaggerItem(Settings settings) {
        super(ToolMaterials.IRON, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float difference = MathHelper.angleBetween(target.bodyYaw, attacker.getHeadYaw());
        if (difference < 60.0F) {
            attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundRegistry.CRIT, SoundCategory.PLAYERS, 0.5F, 1.0F);
            for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld)target.world, target.getPos(), 30.0D)) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeDouble(target.getX());
                buf.writeDouble(target.getY() + 1.5D);
                buf.writeDouble(target.getZ());
                ServerPlayNetworking.send(player, PacketHandler.CRIT, buf);
            }
            target.damage(DamageSource.mob(attacker), this.getCritDamage());
        } else {
            target.damage(DamageSource.mob(attacker), this.getDamage());
        }
        stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        stack.damage(2, miner, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    public float getDamage() {
        return 2.0F;
    }

    public float getCritDamage() {
        return 4.0F;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("item.minecells.assassins_dagger.tooltip").formatted(Formatting.GRAY));
    }
}
