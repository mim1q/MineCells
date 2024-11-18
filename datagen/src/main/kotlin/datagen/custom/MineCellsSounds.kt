package datagen.custom

import tada.lib.generator.ResourceGenerator
import tada.lib.resources.sound.SoundsJsonFile
import tada.lib.resources.sound.soundPath
import tada.lib.resources.sound.sounds
import tada.lib.util.Id

fun SoundsJsonFile.modulatedSimpleSound(id: String) {
  val (_, name) = Id(id)
  val path = soundPath("minecells:$id")
  event(name) {
    sound(path)
    sound(path, pitch = 0.95f)
    sound(path, pitch = 1.05f)
  }
}

fun SoundsJsonFile.modulatedSimpleVariedSound(id: String, variants: Int) {
  val (_, name) = Id(id)
  val path = soundPath("minecells:$id")
  event(name) {
    for (i in 0 until variants) {
      sound("$path/$i")
      sound("$path/$i", pitch = 0.95f)
      sound("$path/$i", pitch = 1.05f)
    }
  }
}

fun SoundsJsonFile.chargeReleaseSounds(prefix: String) {
  modulatedSimpleSound("$prefix.charge")
  modulatedSimpleSound("$prefix.release")
}

fun SoundsJsonFile.dimensionSongs(dimension: String, vararg songs: String) {
  event("music.$dimension") {
    songs.forEach {
      sound("minecraft:music/game/$it", stream = true, volume = 0.6f)
    }
  }
}

fun ResourceGenerator.mineCellsSounds() {
  sounds {
    // Entities
    chargeReleaseSounds("leaping_zombie.leap")
    modulatedSimpleSound("leaping_zombie.death")
    chargeReleaseSounds("shocker")
    modulatedSimpleSound("shocker.death")
    modulatedSimpleSound("grenadier.charge")
    modulatedSimpleSound("disgusting_worm.attack")
    modulatedSimpleSound("disgusting_worm.death")
    chargeReleaseSounds("inquisitor")
    modulatedSimpleSound("kamikaze.wake")
    modulatedSimpleSound("kamikaze.death")
    chargeReleaseSounds("shieldbearer")
    chargeReleaseSounds("mutated_bat")
    chargeReleaseSounds("rancid_rat")
    modulatedSimpleVariedSound("scorpion.charge", 2)
    chargeReleaseSounds("fly")
    modulatedSimpleSound("fly.fly")
    modulatedSimpleSound("fly.release")
    chargeReleaseSounds("sweeper")
    modulatedSimpleSound("sewers_tentacle.death")

    // Conjunctivius
    chargeReleaseSounds("conjunctivius.dash")
    modulatedSimpleSound("conjunctivius.dying")
    modulatedSimpleSound("conjunctivius.death")
    modulatedSimpleSound("conjunctivius.hit")
    modulatedSimpleSound("conjunctivius.shot")
    modulatedSimpleSound("conjunctivius.shout")
    modulatedSimpleSound("conjunctivius.move")

    // Concierge
    modulatedSimpleSound("concierge.leap.charge")
    modulatedSimpleSound("concierge.leap.land")
    chargeReleaseSounds("concierge.shockwave")
    chargeReleaseSounds("concierge.aura")
    chargeReleaseSounds("concierge.punch")
    modulatedSimpleSound("concierge.shout")
    modulatedSimpleSound("concierge.step")

    // Weapons
    chargeReleaseSounds("weapon.bow")
    modulatedSimpleSound("weapon.bow.release")
    modulatedSimpleVariedSound("weapon.swipe", 3)
    chargeReleaseSounds("weapon.tentacle")
    chargeReleaseSounds("weapon.katana")
    modulatedSimpleSound("weapon.frost_blast.release")
    chargeReleaseSounds("weapon.flint")
    modulatedSimpleVariedSound("weapon.hit_floor", 2)

    // Misc
    modulatedSimpleSound("buzz")
    modulatedSimpleSound("crit")
    modulatedSimpleSound("shock")
    modulatedSimpleSound("freeze")
    modulatedSimpleSound("explosion")
    modulatedSimpleSound("rise")
    modulatedSimpleSound("charge")
    modulatedSimpleSound("elevator_start")
    modulatedSimpleSound("elevator_stop")
    modulatedSimpleSound("portal.activate")
    modulatedSimpleSound("portal.use")
    chargeReleaseSounds("teleport")
    modulatedSimpleSound("cell_absorb")
    modulatedSimpleSound("curse_death")
    modulatedSimpleSound("obelisk")

    // Music
    dimensionSongs("prisoners_quarters", "nether/nether_wastes/rubedo")
    dimensionSongs("promenade", "an_ordinary_day")
    dimensionSongs("ramparts", "ancestry")
    dimensionSongs("insufferable_crypt", "nether/crimson_forest/chrysopoeia")
    dimensionSongs("black_bridge", "nether/soulsand_valley/so_below")
  }
}