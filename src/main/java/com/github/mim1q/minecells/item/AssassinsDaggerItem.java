package com.github.mim1q.minecells.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssassinsDaggerItem extends AbstractCritWeaponItem {
    public AssassinsDaggerItem(Settings settings) {
        super(ToolMaterials.IRON, settings);
    }

    public float getDamage() {
        return 5.5F;
    }

    public float getCritDamage() {
        return 7.5F;
    }

    @Override
    public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float difference = MathHelper.angleBetween(target.bodyYaw, attacker.getHeadYaw());
        if (difference < 60.0F) System.out.println("AMOGUSSY");
        return (difference < 60.0F);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("item.minecells.assassins_dagger.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
