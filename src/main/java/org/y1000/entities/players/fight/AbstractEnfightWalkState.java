package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.AbstractPlayerMoveState;
import org.y1000.util.Coordinate;

public abstract class AbstractEnfightWalkState  extends AbstractPlayerMoveState {

    private final Entity target;

    protected static final int REWIND_COOLDOWN = 500;

    public AbstractEnfightWalkState(Coordinate start, Direction towards, int millisPerUnit,
                                   Entity target) {
        super(State.ENFIGHT_WALK, start, towards, millisPerUnit);
        this.target = target;
    }

    protected Entity getTarget() {
        return target;
    }
}
