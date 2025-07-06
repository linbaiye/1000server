package org.y1000.entities.creatures.npc;


import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.event.NpcMovedEvent;

public class DieAction extends AbstractNpcAction {

    private final int animationMillis;

    public DieAction(int animationMillis) {
        this.animationMillis = animationMillis;
    }


    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Die;
    }

    public void die(Npc npc) {
        setTimer(animationMillis);
        //npc.sendEvent(NpcMovedEvent.of(npc));
    }

}
