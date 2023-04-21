package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.dimension.MineCellsDimensions;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.MathUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
  private static final ParticleEffect BORDER_PARTICLE = MineCellsParticles.SPECKLE.get(0x00AAEE);

  public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
    super(world, profile, publicKey);
  }

  private void spawnBorderParticle(Vec3d pos, double range, boolean z) {
    var dx = z ? 0 : random.nextDouble() * range - range / 2;
    var dz = z ? random.nextDouble() * range - range / 2 : 0;
    var dy = 1 + random.nextDouble() * range - range / 2;
    var particlePos = pos.add(dx, dy, dz);
    world.addParticle(
      BORDER_PARTICLE,
      particlePos.x,
      particlePos.y,
      particlePos.z,
      (particlePos.x - pos.x) * 0.03,
      (particlePos.y - pos.y) * 0.03 + 0.01,
      (particlePos.z - pos.z) * 0.03
    );
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void tick(CallbackInfo ci) {
    if (MineCellsDimensions.isMineCellsDimension(world)) {
      var center = MathUtils.getClosestMultiplePosition(getBlockPos(), 1024);
      var dx = getPos().getX() - center.getX() - 0.5D;
      var dz = getPos().getZ() - center.getZ() - 0.5D;
      dx = MathHelper.sign(dx) * 511.5 - dx;
      dz = MathHelper.sign(dz) * 511.5 - dz;
      var adx = Math.abs(dx);
      var adz = Math.abs(dz);
      if (adx < 4) {
        spawnBorderParticle(getPos().add(dx, 0, 0), 2, true);
      }
      if (adz < 4) {
        spawnBorderParticle(getPos().add(0, 0, dz), 2, false);
      }
    }
  }
}
