package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.IAbstractCreatureState;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.creatures.event.NpcChangeStateEvent;
import org.y1000.entities.creatures.monster.NpcStateEnum;

public final class NpcCommonState extends IAbstractCreatureState<Npc> implements NpcState {

    private final OldPlayerStateEnum stat;

    @Override
    public OldPlayerStateEnum stateEnum() {
        return stat;
    }

    @Override
    public void update(Npc npc, int delta) {
        if (elapse(delta)) {
            npc.onActionDone();
        }
    }

    public NpcCommonState(int totalMillis, OldPlayerStateEnum stat) {
        super(totalMillis);
        this.stat = stat;
    }

    @Override
    public boolean attackable() {
        return stateEnum() != OldPlayerStateEnum.DIE;
    }

    @Override
    public NpcStateEnum state() {
        if (stateEnum() == OldPlayerStateEnum.IDLE)
            return NpcStateEnum.Idle;
        if (stateEnum() == OldPlayerStateEnum.ATTACK)
            return NpcStateEnum.Attack;
        if (stateEnum() == OldPlayerStateEnum.DIE)
            return NpcStateEnum.Die;
        if (stateEnum() == OldPlayerStateEnum.Turn)
            return NpcStateEnum.Turn;
        return NpcStateEnum.Idle;
    }

    public static NpcCommonState idle(int total) {
        return new NpcCommonState(total, OldPlayerStateEnum.IDLE);
    }

    public static NpcCommonState attack(int total) {
        return new NpcCommonState(total, OldPlayerStateEnum.ATTACK);
    }

    public static NpcCommonState die(int total) {
        return new NpcCommonState(total, OldPlayerStateEnum.DIE);
    }

    public static NpcCommonState turn(int total) {
        return new NpcCommonState(total, OldPlayerStateEnum.Turn);
    }

    @Override
    public void afterHurt(Npc npc) {
        if (stateEnum() == OldPlayerStateEnum.ATTACK || elapse(npc.getStateMillis(OldPlayerStateEnum.HURT))) {
            npc.onActionDone();
        } else {
            npc.changeState(this);
            npc.emitEvent(NpcChangeStateEvent.of(npc));
        }
    }


}
