package org.y1000.entities.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

@Slf4j
public abstract class AbstractMovableNpcAI extends AbstractNpcAI {

    private Coordinate previous;

    protected AbstractMovableNpcAI(Npc npc) {
        super(npc);
        resetPrevious();
    }

    abstract void onMoveFailed();

    void computePrevious() {
        previous = npc().coordinate().moveBy(npc().direction().opposite());
    }

    void resetPrevious() {
        previous = npc().coordinate();
    }

    void moveOrTurn(Direction dir) {
        if (dir == npc().direction()) {
            if (!changeAbilityOrThrow(NpcMoveAbility.class).tryMove(npc(), dir)) {
                onMoveFailed();
            }
        } else {
            changeAbilityOrThrow(NpcTurnAbility.class).turn(npc(), dir);
        }
    }

    void moveCloser(Coordinate destination) {
        var dir = computeDirectionTo(destination);
        if (dir == null) {
            onMoveFailed();
        } else {
            moveOrTurn(dir);
        }
    }

    Direction computeDirectionTo(Coordinate dest) {
        return npc().coordinate().bestDirectionTo(dest,
                coordinate -> npc().realmMap().movable(coordinate) && !previous.equals(coordinate));
    }
}
