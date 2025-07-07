package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;


public class HurtAction extends AbstractNpcAction {

    private final int animationMillis;

    @Getter
    private NpcActionEnum previousAction;

    public HurtAction(int millis) {
        this.animationMillis = millis;
    }

    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Hurt;
    }

    public void hurt(Npc npc, NpcActionEnum previousAction) {
        setTimer(animationMillis);
        this.previousAction = previousAction;
        npc.sendEvent(NpcStartActionEvent.of(npc, actionEnum()));
    }

    public void hurt(Npc npc, NpcAction actionWhenHurt) {
        setTimer(animationMillis);
        if (actionWhenHurt instanceof HurtAction hurtAbility) {
            this.previousAction = hurtAbility.getPreviousAction();
        } else {
            this.previousAction = actionWhenHurt.actionEnum();
        }
        npc.sendEvent(NpcStartActionEvent.of(npc, actionEnum()));
    }
}
