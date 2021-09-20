package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public final class SoundRegistry {

    // JUMPING ZOMBIE

    public static final Identifier JUMPING_ZOMBIE_MELEE_ID = new Identifier("minecells:jumping_zombie.melee");
    public static final SoundEvent JUMPING_ZOMBIE_MELEE_SOUND_EVENT = new SoundEvent(JUMPING_ZOMBIE_MELEE_ID);

    public static final Identifier JUMPING_ZOMBIE_JUMP_ID = new Identifier("minecells:jumping_zombie.jump");
    public static final SoundEvent JUMPING_ZOMBIE_JUMP_SOUND_EVENT = new SoundEvent(JUMPING_ZOMBIE_JUMP_ID);

    public static final Identifier JUMPING_ZOMBIE_DEATH_ID = new Identifier("minecells:jumping_zombie.death");
    public static final SoundEvent JUMPING_ZOMBIE_DEATH_SOUND_EVENT = new SoundEvent(JUMPING_ZOMBIE_DEATH_ID);

    public static void register() {

        // JUMPING ZOMBIE

        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_MELEE_ID, JUMPING_ZOMBIE_MELEE_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_JUMP_ID, JUMPING_ZOMBIE_JUMP_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, JUMPING_ZOMBIE_DEATH_ID, JUMPING_ZOMBIE_DEATH_SOUND_EVENT);
    }
}
