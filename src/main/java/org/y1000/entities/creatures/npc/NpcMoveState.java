package org.y1000.entities.creatures.npc;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public final class NpcMoveState extends AbstractCreatureMoveState<Npc> implements NpcState {
    private NpcMoveState(Coordinate start,
                             Direction towards,
                             int millisPerUnit) {
        super(State.WALK, start, towards, millisPerUnit);
    }

    @Override
    public void update(Npc npc, int delta) {
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
    public void moveToHurtCoordinate(Npc creature) {
        tryChangeCoordinate(creature, creature.realmMap());
    }

    public static NpcMoveState move(Npc npc, int speed) {
        return new NpcMoveState(npc.coordinate(), npc.direction(), speed);
    }
}
