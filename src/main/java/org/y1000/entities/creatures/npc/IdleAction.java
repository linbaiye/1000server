package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;

public class IdleAction extends AbstractNpcAction {

    private final int animationMillis;

    public IdleAction(int animationMillis) {
        this.animationMillis = animationMillis;
    }

    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Idle;
    }

    public void stayLoopAnimationMillis(Npc npc) {
        stay(npc, animationMillis * 2);
    }

    public void stay(Npc npc, int stateMillis) {
        setTimer(stateMillis);
        npc.sendEvent(NpcStartActionEvent.of(npc, actionEnum()));
    }
}
