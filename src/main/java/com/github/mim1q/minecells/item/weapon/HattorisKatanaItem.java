package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HattorisKatanaItem extends SwordItem {
  private static final String TOOLTIP1_KEY = "item.minecells.hattoris_katana.tooltip1";
  private static final String TOOLTIP2_KEY = "item.minecells.hattoris_katana.tooltip2";
  public HattorisKatanaItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    if (world.isClient()) {
      ParticleUtils.addAura((ClientWorld) world, user.getPos().add(0.0D, 1.0D, 0.0D), ParticleTypes.END_ROD, 1, 3.0D, -0.2D);
    }
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (user.isPlayer()) {
      PlayerEntity player = (PlayerEntity) user;
      player.getItemCooldownManager().set(this, 20);
    }
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    if (user.isPlayer()) {
      PlayerEntity player = (PlayerEntity) user;
      player.getItemCooldownManager().set(this, 20 * 15);

      Vec3d start = player.getPos().add(0.0D, 0.25D, 0.0D);
      Vec3d direction = player.getRotationVec(0.0F).multiply(1.0D, 0.0D, 1.0D).normalize();

      Vec3d hitPos = getHitPos(player, start, direction, 8.0D).subtract(direction.multiply(0.5D));
      this.damageEntities(world, player, start, hitPos);
      if (world.isClient()) {
        spawnTrailParticles(world, start, hitPos);
      } else {
        stack.damage(1, player, (p) -> p.sendToolBreakStatus(user.getActiveHand()));
      }
      player.teleport(hitPos.x, hitPos.y, hitPos.z);
      user.playSound(MineCellsSounds.KATANA_RELEASE, 2.0F, 1.0F);
    }
    return super.finishUsing(stack, world, user);
  }

  private void spawnTrailParticles(World world, Vec3d start, Vec3d hitPos) {
    List<Vec3d> increments = getIncrements(start, hitPos, 20);
    for (Vec3d increment : increments) {
      ParticleUtils.addAura((ClientWorld) world, increment, ParticleTypes.END_ROD, 2, 3.0D, -0.1D);
      float speed = (float) (increment.distanceTo(hitPos) / 200.0F);
      ParticleUtils.addAura((ClientWorld) world, increment, ParticleTypes.CAMPFIRE_COSY_SMOKE, 3, 0.5D, speed);
    }
    ParticleUtils.addParticle((ClientWorld) world, ParticleTypes.FLASH, start.add(0.0D, 1.0D, 0.0D), Vec3d.ZERO);
    ParticleUtils.addParticle((ClientWorld) world, ParticleTypes.FLASH, hitPos.add(0.0D, 1.0D, 0.0D), Vec3d.ZERO);
  }

  private Vec3d getHitPos(PlayerEntity player, Vec3d start, Vec3d direction, double distance) {
    Vec3d end = start.add(direction.multiply(distance));
    HitResult hit1 = player.world.raycast(new RaycastContext(
      start,
      end,
      RaycastContext.ShapeType.COLLIDER,
      RaycastContext.FluidHandling.NONE,
      player
    ));

    HitResult hit2 = player.world.raycast(new RaycastContext(
      start.add(0.0D, 1.0D, 0.0D),
      end.add(0.0D, 1.0D, 0.0D),
      RaycastContext.ShapeType.COLLIDER,
      RaycastContext.FluidHandling.NONE,
      player
    ));

    return hit1.squaredDistanceTo(player) < hit2.squaredDistanceTo(player)
      ? hit1.getPos().multiply(1.0D, 0.0D, 1.0D).add(0.0D, player.getY(), 0.0D)
      : hit2.getPos().multiply(1.0D, 0.0D, 1.0D).add(0.0D, player.getY(), 0.0D);
  }

  private List<Vec3d> getIncrements(Vec3d start, Vec3d end, int count) {
    List<Vec3d> result = new ArrayList<>();
    Vec3d diff = end.subtract(start);
    for (int i = 0; i <= count; i++) {
      result.add(start.add(diff.multiply(i / (double) count)));
    }
    return result;
  }

  private void damageEntities(World world, PlayerEntity user, Vec3d start, Vec3d end) {
    List<LivingEntity> entities = new ArrayList<>();
    List<Vec3d> increments = getIncrements(start, end, 10);
    for (Vec3d pos : increments) {
      world.getOtherEntities(user, Box.of(pos, 1.5D, 1.5D, 1.5D)).forEach(entity -> {
        if (entity instanceof LivingEntity && !entities.contains(entity)) {
          entity.damage(MineCellsDamageSource.katana(user), 10.0F);
          Vec3d direction = pos.subtract(entity.getPos()).normalize();
          ((LivingEntity) entity).takeKnockback(0.5F, direction.getX(), direction.getZ());
          entities.add((LivingEntity) entity);
        }
      });
    }
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 30;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    user.playSound(MineCellsSounds.KATANA_CHARGE, 1.0F, 1.0F);
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable(TOOLTIP1_KEY).formatted(Formatting.GRAY));
    tooltip.add(Text.translatable(TOOLTIP2_KEY).formatted(Formatting.DARK_GRAY));
  }
}
