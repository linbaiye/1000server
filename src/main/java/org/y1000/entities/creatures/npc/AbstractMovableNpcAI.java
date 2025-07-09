package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.npc.AI.AiPathUtil;
import org.y1000.util.Coordinate;

@Slf4j
public abstract class AbstractMovableNpcAI extends AbstractNpcAI {

    private Coordinate previous;

    protected AbstractMovableNpcAI(Npc npc ) {
        super(npc);
        previous = npc.coordinate().moveBy(npc.direction().opposite());
    }

    abstract void onMoveFailed();

    void computePrevious() {
        previous = npc().coordinate().moveBy(npc().direction().opposite());
    }


    void moveCloser(Coordinate destination) {
        var dir = AiPathUtil.computeNextDirection(npc(), destination, previous);
        if (dir == null) {
            onMoveFailed();
            return;
        }
        if (dir == npc().direction()) {
            if (!changeAbilityOrThrow(NpcMoveAbility.class).tryMove(npc(), dir)) {
                onMoveFailed();
            }
        } else {
            log.debug("Turn start for {}.", npc().id());
            changeAbilityOrThrow(NpcTurnAbility.class).turn(npc(), dir);
        }
    }
}
