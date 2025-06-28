package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.IAbstractCreatureState;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.creatures.event.NpcChangeStateEvent;
import org.y1000.entities.creatures.monster.NpcStateEnum;

public final class NpcCommonState extends IAbstractCreatureState<Npc> implements NpcState {

    private final PlayerStateEnum stat;

    @Override
    public PlayerStateEnum stateEnum() {
        return stat;
    }

    @Override
    public void update(Npc npc, int delta) {
        if (elapse(delta)) {
            npc.onActionDone();
        }
    }

    public NpcCommonState(int totalMillis, PlayerStateEnum stat) {
        super(totalMillis);
        this.stat = stat;
    }

    @Override
    public boolean attackable() {
        return stateEnum() != PlayerStateEnum.DIE;
    }

    @Override
    public NpcStateEnum state() {
        if (stateEnum() == PlayerStateEnum.IDLE)
            return NpcStateEnum.Idle;
        if (stateEnum() == PlayerStateEnum.ATTACK)
            return NpcStateEnum.Attack;
        if (stateEnum() == PlayerStateEnum.DIE)
            return NpcStateEnum.Die;
        if (stateEnum() == PlayerStateEnum.Turn)
            return NpcStateEnum.Turn;
        return NpcStateEnum.Idle;
    }

    public static NpcCommonState idle(int total) {
        return new NpcCommonState(total, PlayerStateEnum.IDLE);
    }

    public static NpcCommonState attack(int total) {
        return new NpcCommonState(total, PlayerStateEnum.ATTACK);
    }

    public static NpcCommonState die(int total) {
        return new NpcCommonState(total, PlayerStateEnum.DIE);
    }

    public static NpcCommonState turn(int total) {
        return new NpcCommonState(total, PlayerStateEnum.Turn);
    }

    @Override
    public void afterHurt(Npc npc) {
        if (stateEnum() == PlayerStateEnum.ATTACK || elapse(npc.getStateMillis(PlayerStateEnum.HURT))) {
            npc.onActionDone();
        } else {
            npc.changeState(this);
            npc.emitEvent(NpcChangeStateEvent.of(npc));
        }
    }


}
