package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractCreatureState;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;

public final class NpcCommonState extends AbstractCreatureState implements NpcState {

    private final NpcAnimationEnum stat;

    private final INpc npc;

    @Override
    public NpcAnimationEnum stateEnum() {
        return stat;
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            npc.onActionDone();
        }
    }

    private NpcCommonState(INpc npc, int totalMillis, NpcAnimationEnum stat) {
        super(totalMillis);
        this.stat = stat;
        this.npc = npc;
    }

    private NpcCommonState(INpc npc, NpcAnimationEnum stat) {
        super(npc.getStateMillis(stat));
        this.stat = stat;
        this.npc = npc;
    }


    public static NpcCommonState idle(INpc npc, int total) {
        return new NpcCommonState(npc, total, NpcAnimationEnum.Idle);
    }

    public static NpcCommonState idle(INpc npc) {
        return new NpcCommonState(npc, NpcAnimationEnum.Idle);
    }

    public static NpcCommonState attack(INpc npc) {
        return new NpcCommonState(npc,  NpcAnimationEnum.Attack);
    }

    public static NpcCommonState die(INpc npc, int total) {
        return new NpcCommonState(npc, total, NpcAnimationEnum.Die);
    }

    public static NpcCommonState turn(INpc npc) {
        return new NpcCommonState(npc,  NpcAnimationEnum.Turn);
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
