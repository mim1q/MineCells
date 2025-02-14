package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.FallResetEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.item.MineCellsItemTags;
import com.github.mim1q.minecells.structure.grid.GridBasedStructureUtils;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

import java.util.Comparator;

import static java.lang.Math.abs;


@Mixin(Entity.class)
public abstract class EntityFallResetMixin implements FallResetEntity {
  @Shadow public abstract double getY();
  @Shadow public abstract World getWorld();
  @Shadow public abstract BlockPos getBlockPos();
  @Shadow public abstract void teleport(double destX, double destY, double destZ);

  @Shadow public float fallDistance;

  @Shadow public abstract boolean damage(DamageSource source, float amount);
  @Shadow public abstract void setVelocity(Vec3d velocity);
  @Shadow public abstract EntityType<?> getType();
  @Shadow public abstract double getX();
  @Shadow public abstract float getYaw(float tickDelta);
  @Shadow public abstract double getZ();

  @Shadow public int age;

  @Shadow public abstract void discard();
  @Shadow public abstract boolean isPlayer();
  @Shadow public abstract Text getName();
  @Shadow public abstract void dismountVehicle();

  @Shadow public abstract Vec3d getPos();

  @Unique private Double fallResetY = 0.0;
  @Unique private BlockPos dimensionTpPos = BlockPos.ORIGIN;

  @Inject(
    method = "<init>",
    at = @At("RETURN")
  )
  private void minecells$injectInit(EntityType<?> type, World world, CallbackInfo ci) {
    this.fallResetY = MineCellsDimension.getFallResetHeight(world);
    this.dimensionTpPos = getBlockPos();
  }

  @Inject(
    method = "moveToWorld",
    at = @At("RETURN")
  )
  private void minecells$injectMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
    var result = cir.getReturnValue();
    if (result != null) {
      minecells$initDimensionChange(result, destination);
    }
  }

  @SuppressWarnings("DataFlowIssue")
  @Override
  public void minecells$initDimensionChange(Entity result, ServerWorld destination) {
    ((EntityFallResetMixin) (Object) result).fallResetY = MineCellsDimension.getFallResetHeight(destination);
    ((EntityFallResetMixin) (Object) result).dimensionTpPos = result.getBlockPos();
  }

  @Inject(
    method = "tick",
    at = @At("HEAD")
  )
  private void minecells$injectTick(CallbackInfo ci) {
    //noinspection ConstantValue
    if (getWorld().isClient
      || MineCells.COMMON_CONFIG.disableFallProtection()
      || this.age < 10
      || this.age % 2 == 0
      || fallResetY == null
      || (abs(dimensionTpPos.getX() - getX()) < 4 && abs(dimensionTpPos.getZ() - getZ()) < 4)
      || (((Entity) (Object) this) instanceof PlayerEntity player && (player.isCreative() || player.isSpectator()))
    ) {
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
      //noinspection DataFlowIssue
      if ((Entity) (Object) this instanceof HostileEntity) {
        damage(getWorld().getDamageSources().fall(), 100.0F);
        return;
      }

      var tpPos = minecells$getResetToPos();
      this.dismountVehicle();

      if (this.isPlayer()) {
        MineCells.LOGGER.info("Fall protection mechanic triggered for Player {} at {} from {} in dimension {}",
          this.getName().getString(),
          tpPos.toShortString(),
          getBlockPos().toShortString(),
          getWorld().getRegistryKey().getValue()
        );

        minecells$grantAdvancementCriterion();
      }

      this.teleport(tpPos.getX() + 0.5, tpPos.getY() + 0.5, tpPos.getZ() + 0.5);
      this.setVelocity(Vec3d.ZERO);
      fallDistance = 0.0f;
      damage(getWorld().getDamageSources().fall(), 5.0F);
    }
  }

  @SuppressWarnings("DataFlowIssue")
  @Unique
  private void minecells$grantAdvancementCriterion() {
    var server = getWorld().getServer();
    if (server == null) return;

    var advancement = server.getAdvancementLoader().get(MineCells.createId("unlock/fall_from_the_ramparts"));
    if (advancement == null) return;

    var tracker = ((ServerPlayerEntity) (Object) this).getAdvancementTracker();
    tracker.grantCriterion(advancement, "teleported_up");
  }

  @Unique
  private BlockPos minecells$getResetToPos() {
    var serverWorld = (ServerWorld) getWorld();
    var dimension = MineCellsDimension.of(getWorld());
    var pos = MathUtils.getClosestMultiplePosition(getBlockPos(), 1024);
    var specialPoints = GridBasedStructureUtils.getSpecialPoints(serverWorld, pos, dimension.baseGenerator);

    var point = specialPoints.stream()
      .filter(it -> it.id().equals(SpecialPointIds.CHECKPOINT))
      .map(it -> {
        BlockPos result = new BlockPos(it.offset().add(pos));
        result = new BlockPos(
          result.getX(),
          getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, result.getX(), result.getZ()),
          result.getZ());
        return result;
      })
      .filter(it -> it.getZ() < getZ()
        && getWorld().getBlockState(it.down()).isSideSolidFullSquare(getWorld(), it.down(), Direction.UP)
      )
      .min(Comparator.comparingDouble(it -> it.getSquaredDistance(getPos())));

    return point.orElse(BlockPos.ofFloored(
      dimension.getTeleportPosition(getBlockPos(), serverWorld, SpecialPointIds.ENTRANCE).getLeft())
    );
  }
}
