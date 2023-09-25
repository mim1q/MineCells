package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;


public final class MineCellsSounds {
  // Leaping Zombie
  public static final SoundEvent LEAPING_ZOMBIE_CHARGE = register("leaping_zombie.leap_charge");
  public static final SoundEvent LEAPING_ZOMBIE_RELEASE = register("leaping_zombie.leap_release");
  public static final SoundEvent LEAPING_ZOMBIE_DEATH = register("leaping_zombie.death");
  // Shocker
  public static final SoundEvent SHOCKER_DEATH = register("shocker.death");
  public static final SoundEvent SHOCKER_CHARGE = register("shocker.charge");
  public static final SoundEvent SHOCKER_RELEASE = register("shocker.release");
  // Grenadier
  public static final SoundEvent GRENADIER_CHARGE = register("grenadier.charge");
  // Disgusting Worm
  public static final SoundEvent DISGUSTING_WORM_ATTACK = register("disgusting_worm.attack");
  public static final SoundEvent DISGUSTING_WORM_DEATH = register("disgusting_worm.death");
  // Inquisitor
  public static final SoundEvent INQUISITOR_CHARGE = register("inquisitor.charge");
  public static final SoundEvent INQUISITOR_RELEASE = register("inquisitor.release");
  // Kamikaze
  public static final SoundEvent KAMIKAZE_WAKE = register("kamikaze.wake");
  public static final SoundEvent KAMIKAZE_CHARGE = register("kamikaze.charge");
  public static final SoundEvent KAMIKAZE_DEATH = register("kamikaze.death");
  // Shieldbearer
  public static final SoundEvent SHIELDBEARER_CHARGE = register("shieldbearer.charge");
  public static final SoundEvent SHIELDBEARER_RELEASE = register("shieldbearer.release");
  // Mutated Bat
  public static final SoundEvent MUTATED_BAT_CHARGE = register("mutated_bat.charge");
  public static final SoundEvent MUTATED_BAT_RELEASE = register("mutated_bat.release");
  public static final SoundEvent MUTATED_BAT_WAKE = register("mutated_bat.wake");
  // Rancid Rat
  public static final SoundEvent RANCID_RAT_CHARGE = register("rancid_rat.charge");
  public static final SoundEvent RANCID_RAT_RELEASE = register("rancid_rat.release");
  // Scorpion
  public static final SoundEvent SCORPION_CHARGE = register("scorpion.charge");
  // Sewers Tentacle
  public static final SoundEvent SEWERS_TENTACLE_DEATH = register("sewers_tentacle.death");
  // Flies
  public static final SoundEvent FLY_CHARGE = register("fly.charge");
  public static final SoundEvent FLY_FLY = register("fly.fly");
  public static final SoundEvent FLY_RELEASE = register("fly.release");
  // Sweeper
  public static final SoundEvent SWEEPER_CHARGE = register("sweeper.charge");
  public static final SoundEvent SWEEPER_RELEASE = register("sweeper.release");
  // Conjunctivius
  public static final SoundEvent CONJUNCTIVIUS_DASH_CHARGE = register("conjunctivius.dash_charge");
  public static final SoundEvent CONJUNCTIVIUS_DASH_RELEASE = register("conjunctivius.dash_release");
  public static final SoundEvent CONJUNCTIVIUS_DYING = register("conjunctivius.dying");
  public static final SoundEvent CONJUNCTIVIUS_DEATH = register("conjunctivius.death");
  public static final SoundEvent CONJUNCTIVIUS_HIT = register("conjunctivius.hit");
  public static final SoundEvent CONJUNCTIVIUS_SHOT = register("conjunctivius.shot");
  public static final SoundEvent CONJUNCTIVIUS_SHOUT = register("conjunctivius.shout");
  public static final SoundEvent CONJUNCTIVIUS_MOVE = register("conjunctivius.move");
  // Concierge
  public static final SoundEvent CONCIERGE_LEAP_CHARGE = register("concierge.leap_charge");
  public static final SoundEvent CONCIERGE_LEAP_LAND = register("concierge.leap_land");
  public static final SoundEvent CONCIERGE_SHOCKWAVE_CHARGE = register("concierge.shockwave_charge");
  public static final SoundEvent CONCIERGE_SHOCKWAVE_RELEASE = register("concierge.shockwave_release");
  // Weapons
  public static final SoundEvent BOW_CHARGE = register("weapon.bow.charge");
  public static final SoundEvent BOW_RELEASE = register("weapon.bow.release");
  public static final SoundEvent SWIPE = register("weapon.swipe");
  public static final SoundEvent HIT_FLOOR = register("weapon.hit_floor");

  public static final SoundEvent TENTACLE_CHARGE = register("weapon.tentacle.charge");
  public static final SoundEvent TENTACLE_RELEASE = register("weapon.tentacle.release");

  public static final SoundEvent KATANA_CHARGE = register("weapon.katana.charge");
  public static final SoundEvent KATANA_RELEASE = register("weapon.katana.release");

  public static final SoundEvent FROST_BLAST = register("weapon.frost_blast.release");

  public static final SoundEvent FLINT_CHARGE = register("weapon.flint.charge");
  public static final SoundEvent FLINT_RELEASE = register("weapon.flint.release");

  //Blocks
  public static final SoundEvent PORTAL_ACTIVATE = register("portal.activate");
  public static final SoundEvent PORTAL_USE = register("portal.use");

  // Other
  public static final SoundEvent CRIT = register("crit");
  public static final SoundEvent SHOCK = register("shock");
  public static final SoundEvent FREEZE = register("freeze");
  public static final SoundEvent EXPLOSION = register("explosion");
  public static final SoundEvent ELEVATOR_START = register("elevator_start");
  public static final SoundEvent ELEVATOR_STOP = register("elevator_stop");
  public static final SoundEvent BUZZ = register("buzz");
  public static final SoundEvent TELEPORT_CHARGE = register("teleport.charge");
  public static final SoundEvent TELEPORT_RELEASE = register("teleport.release");
  public static final SoundEvent RISE = register("rise");
  public static final SoundEvent CHARGE = register("charge");
  public static final SoundEvent CELL_ABSORB = register("cell_absorb");
  public static final SoundEvent CURSE_DEATH = register("curse_death");
  public static final SoundEvent OBELISK = register("obelisk");

  // Music
  // This is not added to the assets by default and has to be included in a custom Resource Pack to be played.
  public static final MusicSound PRISONERS_QUARTERS = registerMusic("music.prisoners_quarters");
  public static final MusicSound PROMENADE = registerMusic("music.promenade");
  public static final MusicSound RAMPARTS = registerMusic("music.ramparts");
  public static final MusicSound INSUFFERABLE_CRYPT = registerMusic("music.insufferable_crypt");
  public static final MusicSound BLACK_BRIDGE = registerMusic("music.black_bridge");

  public static SoundEvent register(String name) {
    Identifier id = MineCells.createId(name);
    return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
  }

  public static MusicSound registerMusic(String name) {
    var id = MineCells.createId(name);
    var sound = Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    return new MusicSound(sound, 0, 0, true);
  }

  public static void init() { }
}
