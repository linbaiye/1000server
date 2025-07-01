package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractCreatureState;
import org.y1000.entities.creatures.monster.NpcStateEnum;

public final class NpcCommonState extends AbstractCreatureState implements NpcState {

    private final NpcStateEnum stat;

    private final Npc npc;

    @Override
    public NpcStateEnum stateEnum() {
        return stat;
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            npc.onActionDone();
        }
    }

    private NpcCommonState(Npc npc, int totalMillis, NpcStateEnum stat) {
        super(totalMillis);
        this.stat = stat;
        this.npc = npc;
    }

    private NpcCommonState(Npc npc, NpcStateEnum stat) {
        super(npc.getStateMillis(stat));
        this.stat = stat;
        this.npc = npc;
    }


    public static NpcCommonState idle(Npc npc, int total) {
        return new NpcCommonState(npc, total, NpcStateEnum.Idle);
    }

    public static NpcCommonState idle(Npc npc) {
        return new NpcCommonState(npc, NpcStateEnum.Idle);
    }

    public static NpcCommonState attack(Npc npc) {
        return new NpcCommonState(npc,  NpcStateEnum.Attack);
    }

    public static NpcCommonState die(Npc npc, int total) {
        return new NpcCommonState(npc, total, NpcStateEnum.Die);
    }

    public static NpcCommonState turn(Npc npc) {
        return new NpcCommonState(npc,  NpcStateEnum.Turn);
    }

    @Override
    public void afterHurt(Npc npc) {
//        if (stateEnum() == OldPlayerStateEnum.ATTACK || elapse(npc.getStateMillis(OldPlayerStateEnum.HURT))) {
//            npc.onActionDone();
//        } else {
//            npc.changeState(this);
//            npc.emitEvent(NpcChangeStateEvent.of(npc));
//        }
    }


}
