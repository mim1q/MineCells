package com.github.mim1q.minecells.registry;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public final class SoundRegistry {

    //region Jumping Zombie

    public static final Identifier JUMPING_ZOMBIE_MELEE_ID = new Identifier("minecells:jumping_zombie.melee");
    public static final SoundEvent JUMPING_ZOMBIE_MELEE = new SoundEvent(JUMPING_ZOMBIE_MELEE_ID);

    public static final Identifier JUMPING_ZOMBIE_JUMP_ID = new Identifier("minecells:jumping_zombie.jump");
    public static final SoundEvent JUMPING_ZOMBIE_JUMP = new SoundEvent(JUMPING_ZOMBIE_JUMP_ID);

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

        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_MELEE_ID, JUMPING_ZOMBIE_MELEE);
        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_JUMP_ID, JUMPING_ZOMBIE_JUMP);
        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_DEATH_ID, JUMPING_ZOMBIE_DEATH);

        //endregion
        //region Shocker

        Registry.register(Registry.SOUND_EVENT, SHOCKER_CHARGE_ID, SHOCKER_CHARGE);
        Registry.register(Registry.SOUND_EVENT, SHOCKER_RELEASE_ID, SHOCKER_RELEASE);
        Registry.register(Registry.SOUND_EVENT, SHOCKER_DEATH_ID, SHOCKER_DEATH);

        //endregion
    }
}
