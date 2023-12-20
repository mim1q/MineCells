package com.github.mim1q.minecells.structure;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridBasedStructure;
import com.github.mim1q.minecells.structure.grid.GridPiece;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Locale;

public class MineCellsStructures {
  // Structures
  public static final StructureType<MineCellsBigJigsawStructure> BIG_JIGSAW = registerStructure("big_jigsaw", MineCellsBigJigsawStructure.CODEC);

  // Prisoners' Quarters
  public static final StructureType<GridBasedStructure> PRISON = registerStructure("prison", GridBasedStructure.PRISON_CODEC);

  // Promenade of the Condemned
  public static final StructureType<GridBasedStructure> PROMENADE_SPAWN = registerStructure("promenade_spawn", GridBasedStructure.PROMENADE_SPAWN_CODEC);
  public static final StructureType<GridBasedStructure> PROMENADE_OVERGROUND = registerStructure("promenade_overground", GridBasedStructure.PROMENADE_OVERGROUND_CODEC);
  public static final StructureType<GridBasedStructure> PROMENADE_UNDERGROUND = registerStructure("promenade_underground", GridBasedStructure.PROMENADE_UNDERGROUND_CODEC);
  public static final StructureType<GridBasedStructure> PROMENADE_PIT = registerStructure("promenade_pit", GridBasedStructure.PROMENADE_PIT_CODEC);
  public static final StructureType<GridBasedStructure> PROMENADE_WALL_X = registerStructure("promenade_wall_x", GridBasedStructure.PROMENADE_WALL_X_CODEC);
  public static final StructureType<GridBasedStructure> PROMENADE_WALL_Z = registerStructure("promenade_wall_z", GridBasedStructure.PROMENADE_WALL_Z_CODEC);

  // Ramparts
  public static final StructureType<GridBasedStructure> RAMPARTS = registerStructure("ramparts", GridBasedStructure.RAMPARTS_CODEC);
  public static final StructureType<GridBasedStructure> RAMPARTS_SECOND = registerStructure("ramparts_second", GridBasedStructure.RAMPARTS_SECOND_CODEC);

  // Structure Pieces
  public static final StructurePieceType GRID_PIECE = registerPiece(GridPiece::new, "grid_generator_piece");

  private static <S extends Structure> StructureType<S> registerStructure(String id, Codec<S> codec) {
    return Registry.register(Registries.STRUCTURE_TYPE, MineCells.createId(id), () -> codec);
  }

  private static StructurePieceType registerPiece(StructurePieceType type, String id) {
    return Registry.register(Registries.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), type);
  }

  public static void init() {
  }
}
