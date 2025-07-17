package org.y1000.entities.creatures.npc.spell;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.CooldownAbility;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.event.NpcSoundEvent;

import java.util.concurrent.ThreadLocalRandom;

public class NpcSoundAbility implements CooldownAbility  {
    private final String sound;

    private int timeToSound;

    public NpcSoundAbility(String sound) {
        Validate.notNull(sound);
        this.sound = sound;
        resetTime();
    }

    private void resetTime() {
        timeToSound = ThreadLocalRandom.current().nextInt(10, 26) * 1000;
    }

    @Override
    public void cooldown(int delta) {
        timeToSound = timeToSound > delta ? timeToSound - delta : 0;
    }

    public void trySound(Npc npc) {
        if (timeToSound > 0)
           return;
        npc.sendEvent(NpcSoundEvent.of(npc, sound));
        resetTime();
    }
}
