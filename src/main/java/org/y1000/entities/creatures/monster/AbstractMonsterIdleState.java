package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public abstract class AbstractMonsterIdleState extends AbstractMonsterState {

    private final Coordinate from;

    public AbstractMonsterIdleState(int totalMillis, Coordinate from) {
        super(totalMillis, State.IDLE);
        this.from = from;
    }

    /**
     * From what coordinate the monster has moved to current coordinate.
     */
    protected Coordinate getFrom() {
        return from;
    }
}
