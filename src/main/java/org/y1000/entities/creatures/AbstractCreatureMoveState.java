package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.players.State;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public abstract class AbstractCreatureMoveState<C extends AbstractCreature> extends AbstractCreateState<C> {

    private final State state;

    private final long millisPerUnit;

    private final Direction towards;


    public AbstractCreatureMoveState(State state,
                                     long millisPerUnit, Direction towards) {
        this.state = state;
        this.millisPerUnit = millisPerUnit;
        this.towards = towards;
    }

    @Override
    public State stateEnum() {
        return state;
    }

    protected long millisPerUnit() {
        return millisPerUnit;
    }


    protected boolean tryChangeCoordinate(C c, RealmMap realmMap) {
        Coordinate next = c.coordinate().moveBy(towards);
        if (realmMap.movable(next)) {
            realmMap.free(c);
            c.changeCoordinate(next);
            realmMap.occupy(c);
            return true;
        }
        return false;
    }

    protected void walkMillis(C c, long delta) {
        if (elapsedMillis() == 0) {
            c.changeDirection(towards);
        }
        if (elapsedMillis() < millisPerUnit)
            elapse(delta);
    }
}
