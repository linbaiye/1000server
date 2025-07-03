package org.y1000.entities.creatures.npc;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.util.Coordinate;

public final class NpcMoveState extends AbstractCreatureMoveState implements NpcState {

    private final INpc npc;
    @Override
    public void update(int delta) {
        if (elapsedMillis() == 0) {
            npc.realmMap().free(npc);
        }
        if (!elapse(delta)) {
            return;
        }
        if (tryChangeCoordinate(npc, npc.realmMap())) {
            npc.onActionDone();
        } else {
            npc.onMoveFailed();
        }
    }

    @Override
    public NpcActionEnum stateEnum() {
        return NpcActionEnum.Move;
    }
    private NpcMoveState(Coordinate start,
                         Direction towards,
                         int millisPerUnit,
                         INpc npc) {
        super(start, towards, millisPerUnit);
        this.npc = npc;
    }

//
//
//    @Override
//    public void moveToHurtCoordinate(Npc creature) {
//        tryChangeCoordinate(creature, creature.realmMap());
//    }

//
    public static NpcMoveState move(INpc npc, int millis) {
        return new NpcMoveState(npc.coordinate(), npc.direction(), millis, npc);
    }
}
