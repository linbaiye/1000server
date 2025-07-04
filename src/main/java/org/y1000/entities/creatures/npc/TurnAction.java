package org.y1000.entities.creatures.npc;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;

public class TurnAction extends AbstractNpcAction {
    private final int animationMillis;

    public TurnAction(int animationMillis) {
        this.animationMillis = animationMillis;
    }

    public void turn(Npc npc,
                     Direction direction) {
        npc.setDirection(direction);
        npc.sendEvent(NpcStartActionEvent.of(npc, actionEnum()));
        setTimer(animationMillis);
    }

    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Turn;
    }
}
