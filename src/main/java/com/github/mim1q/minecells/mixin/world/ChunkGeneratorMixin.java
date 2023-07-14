package com.github.mim1q.minecells.mixin.world;

import com.github.mim1q.minecells.world.placement.InsideGridPlacement;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
  @Shadow protected abstract List<StructurePlacement> getStructurePlacement(RegistryEntry<Structure> structureEntry, NoiseConfig noiseConfig);

  @Inject(
    method = "locateStructure(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/registry/RegistryEntryList;Lnet/minecraft/util/math/BlockPos;IZ)Lcom/mojang/datafixers/util/Pair;",
    at = @At("RETURN"),
    cancellable = true)
  private void minecells$injectLocateStructure(ServerWorld world, RegistryEntryList<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures, CallbackInfoReturnable<@Nullable Pair<BlockPos, RegistryEntry<Structure>>> cir) {
    var returnValue = cir.getReturnValue();
    if (returnValue == null) {
      structures.stream().forEach(entry -> {
        getStructurePlacement(entry, world.getChunkManager().getNoiseConfig()).forEach(
          placement -> {
            if (placement instanceof InsideGridPlacement gridPlacement) {
              var closest = gridPlacement.getClosestPosition(new ChunkPos(center), world.getSeed());
              if (closest != null) {
                cir.setReturnValue(Pair.of(closest, entry));
              }
            }
          }
        );
      });
    }
  }
}
