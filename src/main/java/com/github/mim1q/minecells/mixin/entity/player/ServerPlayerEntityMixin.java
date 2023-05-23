package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.MineCellsBorderEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimensions;
import com.github.mim1q.minecells.registry.MineCellsGameRules;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
  @Shadow public abstract boolean isInvulnerableTo(DamageSource damageSource);
  @Shadow public abstract ServerWorld getWorld();
  @Shadow public abstract RegistryKey<World> getSpawnPointDimension();
  @Shadow public abstract @Nullable BlockPos getSpawnPointPosition();
  @Shadow public abstract float getSpawnAngle();
  @Shadow public abstract boolean isCreative();
  @Shadow public abstract boolean isSpectator();
  @Shadow public abstract void sendMessage(Text message);

  public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
    super(world, pos, yaw, gameProfile, publicKey);
  }

  @Inject(method = "attack", at = @At("RETURN"))
  public void attack(Entity target, CallbackInfo ci) {
    this.removeStatusEffect(MineCellsStatusEffects.ASSASSINS_STRENGTH);
  }

  @Inject(method = "damage", at = @At("HEAD"))
  public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (
      this.age < 60
      && source == DamageSource.IN_WALL
      && this.world.getGameRules().getBoolean(MineCellsGameRules.SUFFOCATION_FIX)
      && !this.isCreative()
      && !this.isSpectator()
      && MineCellsDimensions.isMineCellsDimension(this.getWorld())
    ) {
      MinecraftServer server = this.getServer();
      if (server == null) {
        return;
      }
      BlockPos spawnPos = this.getSpawnPointPosition();
      if (spawnPos == null) {
        spawnPos = this.getWorld().getSpawnPos();
      }
      ServerWorld targetWorld = server.getWorld(this.getSpawnPointDimension());
      TeleportTarget targetPos = new TeleportTarget(
        Vec3d.ofCenter(spawnPos),
        Vec3d.ZERO,
        this.getSpawnAngle(),
        0.0F
      );
      FabricDimensions.teleport(this, targetWorld, targetPos);
      MineCells.LOGGER.info("Teleporting " + this.getName().getString() + " to their spawnpoint");
      this.sendMessage(
        Text.literal("[Mine Cells] ").formatted(Formatting.RED).append(
          Text.translatable("chat.minecells.suffocation_fix_message").formatted(Formatting.WHITE)
        )
      );
    }
  }

  @Inject(method = {"requestTeleport", "requestTeleportAndDismount"}, at = @At("HEAD"), cancellable = true)
  private void minecells$cancelRequestTeleport(double destX, double destY, double destZ, CallbackInfo ci) {
    if (((MineCellsBorderEntity)this).getMineCellsBorder().getDistanceInsideBorder(destX, destZ) < 2.0D) {
      ci.cancel();
    }
  }
}
