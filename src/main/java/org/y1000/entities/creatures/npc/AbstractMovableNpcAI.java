package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.AI.AiPathUtil;
import org.y1000.util.Coordinate;

public abstract class AbstractMovableNpcAI extends AbstractNpcAI {

    private Coordinate previous;

    protected AbstractMovableNpcAI(Npc npc ) {
        super(npc);
        previous = npc.coordinate().moveBy(npc.direction().opposite());
    }

    abstract void noDirection();

    abstract void directionNotMovable();

    void computePrevious() {
        previous = npc().coordinate().moveBy(npc().direction().opposite());
    }

    void moveCloser(Coordinate destination) {
        var dir = AiPathUtil.computeNextMoveDirection(npc(), destination, previous);
        if (dir == null) {
            noDirection();
            return;
        }
        if (dir == npc().direction()) {
            if (!changeAbilityOrThrow(NpcMoveAbility.class).tryNormalMove(npc(), dir)) {
                directionNotMovable();
            }
        } else {
            changeAbilityOrThrow(NpcTurnAbility.class).turn(npc(), dir);
        }
    }
}
