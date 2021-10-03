package com.github.mim1q.minecells.registry;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public final class SoundRegistry {

    //region Jumping Zombie

    public static final Identifier JUMPING_ZOMBIE_MELEE_CHARGE_ID = new Identifier("minecells:jumping_zombie.melee_charge");
    public static final SoundEvent JUMPING_ZOMBIE_MELEE_CHARGE = new SoundEvent(JUMPING_ZOMBIE_MELEE_CHARGE_ID);

    public static final Identifier JUMPING_ZOMBIE_MELEE_RELEASE_ID = new Identifier("minecells:jumping_zombie.melee_release");
    public static final SoundEvent JUMPING_ZOMBIE_MELEE_RELEASE = new SoundEvent(JUMPING_ZOMBIE_MELEE_RELEASE_ID);

    public static final Identifier JUMPING_ZOMBIE_JUMP_CHARGE_ID = new Identifier("minecells:jumping_zombie.jump_charge");
    public static final SoundEvent JUMPING_ZOMBIE_JUMP_CHARGE = new SoundEvent(JUMPING_ZOMBIE_JUMP_CHARGE_ID);

    public static final Identifier JUMPING_ZOMBIE_JUMP_RELEASE_ID = new Identifier("minecells:jumping_zombie.jump_release");
    public static final SoundEvent JUMPING_ZOMBIE_JUMP_RELEASE = new SoundEvent(JUMPING_ZOMBIE_JUMP_RELEASE_ID);

    public static final Identifier JUMPING_ZOMBIE_DEATH_ID = new Identifier("minecells:jumping_zombie.death");
    public static final SoundEvent JUMPING_ZOMBIE_DEATH = new SoundEvent(JUMPING_ZOMBIE_DEATH_ID);

    //endregion

    //region Shocker

    public static final Identifier SHOCKER_DEATH_ID = new Identifier("minecells:shocker.death");
    public static final SoundEvent SHOCKER_DEATH = new SoundEvent(SHOCKER_DEATH_ID);

    public static final Identifier SHOCKER_CHARGE_ID = new Identifier("minecells:shocker.charge");
    public static final SoundEvent SHOCKER_CHARGE = new SoundEvent(SHOCKER_CHARGE_ID);

    public static final Identifier SHOCKER_RELEASE_ID = new Identifier("minecells:shocker.release");
    public static final SoundEvent SHOCKER_RELEASE = new SoundEvent(SHOCKER_RELEASE_ID);

    //endregion

    public static void register() {

        //region Jumping Zombie

        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_MELEE_CHARGE_ID, JUMPING_ZOMBIE_MELEE_CHARGE);
        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_MELEE_RELEASE_ID, JUMPING_ZOMBIE_MELEE_RELEASE);
        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_JUMP_CHARGE_ID, JUMPING_ZOMBIE_JUMP_CHARGE);
        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_JUMP_RELEASE_ID, JUMPING_ZOMBIE_JUMP_RELEASE);
        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_DEATH_ID, JUMPING_ZOMBIE_DEATH);

        //endregion
        //region Shocker

        Registry.register(Registry.SOUND_EVENT, SHOCKER_CHARGE_ID, SHOCKER_CHARGE);
        Registry.register(Registry.SOUND_EVENT, SHOCKER_RELEASE_ID, SHOCKER_RELEASE);
        Registry.register(Registry.SOUND_EVENT, SHOCKER_DEATH_ID, SHOCKER_DEATH);

        //endregion
    }
}
