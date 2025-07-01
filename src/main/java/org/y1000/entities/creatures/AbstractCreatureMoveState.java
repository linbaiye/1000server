package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public abstract class AbstractCreatureMoveState extends AbstractCreatureState {

    private final Direction towards;

    private final Coordinate start;

    public AbstractCreatureMoveState(Coordinate start,
                                     Direction towards,
                                     int millisPerUnit) {
        super(millisPerUnit);
        this.towards = towards;
        this.start = start;
    }


    protected Coordinate getStart() {
        return start;
    }

    protected boolean tryChangeCoordinate(Creature c, RealmMap realmMap) {
        Coordinate next = c.coordinate().moveBy(towards);
        boolean movable = realmMap.movable(next);
        if (movable)
            c.changeCoordinate(next);
        else
            c.changeCoordinate(c.coordinate());
        return movable;
    }

    protected boolean walkMillis(Creature c, int delta) {
        if (elapsedMillis() == 0) {
            c.changeDirection(towards);
        }
        if (elapsedMillis() < totalMillis())
            elapse(delta);
        return elapsedMillis() >= totalMillis();
    }
}
