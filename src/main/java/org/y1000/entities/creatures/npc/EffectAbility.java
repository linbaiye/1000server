package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.event.NpcActivateEffectEvent;

public class EffectAbility {

    private final String effectName;

    private int counter = 0;

    public EffectAbility(String effectName) {
        this.effectName = effectName;
    }

    public void tryApply(Npc npc) {
        if (++counter < 2)
            return;
        counter = 0;
        npc.sendEvent(NpcActivateEffectEvent.of(npc, effectName, 1000));
    }
}
