package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.entities.creatures.npc.event.NpcLifeBarEvent;
import org.y1000.entities.creatures.npc.event.NpcSoundEvent;
import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;

public final class NpcDieAbility extends AbstractNonMoveAbility {
    private final String sound;

    private final int timeMillis;

    public NpcDieAbility(NpcAnimation animation, String sound) {
        super(animation);
        this.sound = StringUtils.isEmpty(sound) ? null : sound;
        timeMillis = animation.getActualMillis() + 5000;
    }

    public void apply(Npc npc) {
        if (sound != null)
            npc.sendEvent(NpcSoundEvent.of(npc, sound));
        startAnimation(timeMillis);
        npc.sendEvent(NpcStartActionEvent.of(npc, NpcAnimationEnum.Die));
        npc.sendEvent(NpcLifeBarEvent.die(npc));
    }

}
