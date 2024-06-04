package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public abstract class AbstractCreatureMoveState<C extends Creature> extends AbstractCreateState<C> {

    private final State state;

    private final Direction towards;

    private final Coordinate start;

    public AbstractCreatureMoveState(State state,
                                     Coordinate start,
                                     Direction towards, int millisPerUnit) {
        super(millisPerUnit);
        this.state = state;
        this.towards = towards;
        this.start = start;
    }

    @Override
    public State stateEnum() {
        return state;
    }

    protected Coordinate getStart() {
        return start;
    }

    protected boolean tryChangeCoordinate(C c, RealmMap realmMap) {
        Coordinate next = c.coordinate().moveBy(towards);
        if (realmMap.movable(next)) {
            c.changeCoordinate(next);
            realmMap.occupy(c);
            return true;
        }
        return false;
    }


    protected boolean walkMillis(C c, int delta) {
        if (elapsedMillis() == 0) {
            c.changeDirection(towards);
        }
        if (elapsedMillis() < totalMillis())
            elapse(delta);
        return elapsedMillis() >= totalMillis();
    }
}
