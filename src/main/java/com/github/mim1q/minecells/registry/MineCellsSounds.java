package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public final class MineCellsSounds {
  // Leaping Zombie
  public static final SoundEvent LEAPING_ZOMBIE_CHARGE = init("leaping_zombie.leap_charge");
  public static final SoundEvent LEAPING_ZOMBIE_RELEASE = init("leaping_zombie.leap_release");
  public static final SoundEvent LEAPING_ZOMBIE_DEATH = init("leaping_zombie.death");
  // Shocker
  public static final SoundEvent SHOCKER_DEATH = init("shocker.death");
  public static final SoundEvent SHOCKER_CHARGE = init("shocker.charge");
  public static final SoundEvent SHOCKER_RELEASE = init("shocker.release");
  // Grenadier
  public static final SoundEvent GRENADIER_CHARGE = init("grenadier.charge");
  // Disgusting Worm
  public static final SoundEvent DISGUSTING_WORM_ATTACK = init("disgusting_worm.attack");
  public static final SoundEvent DISGUSTING_WORM_DEATH = init("disgusting_worm.death");
  // Inquisitor
  public static final SoundEvent INQUISITOR_CHARGE = init("inquisitor.charge");
  public static final SoundEvent INQUISITOR_RELEASE = init("inquisitor.release");
  // Kamikaze
  public static final SoundEvent KAMIKAZE_WAKE = init("kamikaze.wake");
  public static final SoundEvent KAMIKAZE_CHARGE = init("kamikaze.charge");
  public static final SoundEvent KAMIKAZE_DEATH = init("kamikaze.death");
  // Shieldbearer
  public static final SoundEvent SHIELDBEARER_CHARGE = init("shieldbearer.charge");
  public static final SoundEvent SHIELDBEARER_RELEASE = init("shieldbearer.release");
  // Mutated Bat
  public static final SoundEvent MUTATED_BAT_CHARGE = init("mutated_bat.charge");
  public static final SoundEvent MUTATED_BAT_RELEASE = init("mutated_bat.release");
  public static final SoundEvent MUTATED_BAT_WAKE = init("mutated_bat.wake");
  // Rancid Rat
  public static final SoundEvent RANCID_RAT_CHARGE = init("rancid_rat.charge");
  public static final SoundEvent RANCID_RAT_RELEASE = init("rancid_rat.release");
  // Scorpion
  public static final SoundEvent SCORPION_CHARGE = init("scorpion.charge");
  // Sewers Tentacle
  public static final SoundEvent SEWERS_TENTACLE_DEATH = init("sewers_tentacle.death");
  // Conjunctivius
  public static final SoundEvent CONJUNCTIVIUS_DASH_CHARGE = init("conjunctivius.dash_charge");
  public static final SoundEvent CONJUNCTIVIUS_DASH_RELEASE = init("conjunctivius.dash_release");
  public static final SoundEvent CONJUNCTIVIUS_DYING = init("conjunctivius.dying");
  public static final SoundEvent CONJUNCTIVIUS_DEATH = init("conjunctivius.death");
  public static final SoundEvent CONJUNCTIVIUS_HIT = init("conjunctivius.hit");
  public static final SoundEvent CONJUNCTIVIUS_SHOT = init("conjunctivius.shot");
  public static final SoundEvent CONJUNCTIVIUS_SHOUT = init("conjunctivius.shout");
  public static final SoundEvent CONJUNCTIVIUS_MOVE = init("conjunctivius.move");
  // Weapons
  public static final SoundEvent BOW_CHARGE = init("weapon.bow.charge");
  public static final SoundEvent BOW_RELEASE = init("weapon.bow.release");
  public static final SoundEvent SWIPE = init("weapon.swipe");
  // Other
  public static final SoundEvent CRIT = init("crit");
  public static final SoundEvent SHOCK = init("shock");
  public static final SoundEvent EXPLOSION = init("explosion");
  public static final SoundEvent ELEVATOR_START = init("elevator_start");
  public static final SoundEvent ELEVATOR_STOP = init("elevator_stop");
  public static final SoundEvent BUZZ = init("buzz");
  public static final SoundEvent TELEPORT_CHARGE = init("teleport.charge");
  public static final SoundEvent TELEPORT_RELEASE = init("teleport.release");
  public static final SoundEvent RISE = init("rise");
  public static final SoundEvent CHARGE = init("charge");
  public static final SoundEvent CELL_ABSORB = init("cell_absorb");

  public static SoundEvent init(String name) {
    Identifier id = MineCells.createId(name);
    return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
  }

  public static void init() { }
}
