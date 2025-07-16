package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractCreatureState;

public final class NpcCommonState extends AbstractCreatureState implements NpcState {

    private final NpcAction stat;

    private final INpc npc;

    @Override
    public NpcAction stateEnum() {
        return stat;
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            npc.onActionDone();
        }
    }

    private NpcCommonState(INpc npc, int totalMillis, NpcAction stat) {
        super(totalMillis);
        this.stat = stat;
        this.npc = npc;
    }

    private NpcCommonState(INpc npc, NpcAction stat) {
        super(npc.getStateMillis(stat));
        this.stat = stat;
        this.npc = npc;
    }


    public static NpcCommonState idle(INpc npc, int total) {
        return new NpcCommonState(npc, total, NpcAction.Idle);
    }

    public static NpcCommonState idle(INpc npc) {
        return new NpcCommonState(npc, NpcAction.Idle);
    }

    public static NpcCommonState attack(INpc npc) {
        return new NpcCommonState(npc,  NpcAction.Attack);
    }

    public static NpcCommonState die(INpc npc, int total) {
        return new NpcCommonState(npc, total, NpcAction.Die);
    }

    public static NpcCommonState turn(INpc npc) {
        return new NpcCommonState(npc,  NpcAction.Turn);
    }

    @Override
    public void afterHurt(INpc npc) {
//        if (stateEnum() == OldPlayerStateEnum.ATTACK || elapse(npc.getStateMillis(OldPlayerStateEnum.HURT))) {
//            npc.onActionDone();
//        } else {
//            npc.changeState(this);
//            npc.emitEvent(NpcChangeStateEvent.of(npc));
//        }
    }


}
