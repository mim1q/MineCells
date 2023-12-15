package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.accessor.FallResetHeightEntityAccessor;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.item.MineCellsItemTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class EntityFallResetMixin implements FallResetHeightEntityAccessor {
  @Shadow
  public abstract double getY();

  @Shadow
  public abstract World getWorld();

  @Shadow
  public abstract ChunkPos getChunkPos();

  @Shadow
  public abstract BlockPos getBlockPos();

  @Shadow
  public abstract void teleport(double destX, double destY, double destZ);

  @Shadow
  public float fallDistance;

  @Shadow
  public abstract boolean damage(DamageSource source, float amount);

  @Shadow
  public abstract void setVelocity(Vec3d velocity);

  @Shadow
  public abstract EntityType<?> getType();

  @Shadow
  public abstract double getX();

  @Shadow
  public abstract float getYaw(float tickDelta);

  @Shadow
  public abstract double getZ();

  @Shadow
  public int age;

  @Shadow
  public abstract void discard();

  @Unique
  private Double fallResetY = 0.0;
  @Unique
  private BlockPos lastSolidBlock;

  @Inject(
    method = "<init>",
    at = @At("RETURN")
  )
  private void minecells$injectInit(EntityType<?> type, World world, CallbackInfo ci) {
    this.fallResetY = MineCellsDimension.getFallResetHeight(world);
  }

  @Inject(
    method = "moveToWorld",
    at = @At("RETURN")
  )
  private void minecells$injectMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
    var result = cir.getReturnValue();
    ((EntityFallResetMixin) (Object) result).fallResetY = MineCellsDimension.getFallResetHeight(destination);
  }

  @Inject(
    method = "tick",
    at = @At("HEAD")
  )
  private void minecells$injectTick(CallbackInfo ci) {
    if (
      getWorld().isClient
        || fallResetY == null
        || (((Entity) (Object) this) instanceof PlayerEntity player && (
        player.isCreative() || player.isSpectator())
      )) {
      return;
    }
    if (getY() < fallResetY) {
      if (this.getType() == EntityType.ITEM) {
        if (age % 20 != 0) {
          return;
        }
        //noinspection DataFlowIssue
        if (((ItemEntity) (Object) this).getStack().isIn(MineCellsItemTags.DISCARD_IN_HIGH_DIMENSIONS)) {
          this.discard();
          return;
        }
        var nearestPlayer = getWorld().getClosestPlayer(getX(), getY(), getZ(), 180.0, false);
        if (nearestPlayer == null) {
          return;
        }
        this.teleport(nearestPlayer.getX(), nearestPlayer.getY(), nearestPlayer.getZ());
        this.setVelocity(Vec3d.ZERO);
        fallDistance = 0.0f;
        return;
      }
      if ((Entity) (Object) this instanceof HostileEntity) {
        damage(getWorld().getDamageSources().fall(), 100.0F);
        return;
      }
      var tpPos = minecells$getResetToPos();
      this.teleport(tpPos.getX() + 0.5, tpPos.getY() + 0.5, tpPos.getZ() + 0.5);
      this.setVelocity(Vec3d.ZERO);
      fallDistance = 0.0f;
      damage(getWorld().getDamageSources().fall(), 5.0F);
    } else if (
      getY() > fallResetY + 5
        && getWorld().getBlockState(getBlockPos().down()).isSolidBlock(getWorld(), getBlockPos())
    ) {
      lastSolidBlock = getBlockPos();
    }
  }

  @Unique
  private BlockPos minecells$getResetToPos() {
    BlockPos resetToPos = null;
    if (lastSolidBlock != null && lastSolidBlock.isWithinDistance(getBlockPos().withY(lastSolidBlock.getY()), 32)) {
      resetToPos = lastSolidBlock.withY(getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, lastSolidBlock.getX(), lastSolidBlock.getZ()));
    }
    if (resetToPos == null || resetToPos.getY() < fallResetY) {
      var pos = this.getChunkPos().getBlockPos(8, 0, 8);
      for (var offset : BlockPos.iterateOutwards(BlockPos.ORIGIN, 3, 0, 3)) {
        var checkedPos = pos.add(offset.multiply(16));
        checkedPos = checkedPos.withY(getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, checkedPos.getX(), checkedPos.getZ()));
        if (checkedPos.getY() > fallResetY) {
          resetToPos = checkedPos;
        }
      }
    }
    if (resetToPos == null || resetToPos.getY() < fallResetY) {
      resetToPos = BlockPos.ofFloored(MineCellsDimension.of(getWorld()).getTeleportPosition(getBlockPos(), (ServerWorld) getWorld()));
    }

    return resetToPos;
  }

  @Override
  public void setFallResetY(Double y) {
    this.fallResetY = y;
  }
}
