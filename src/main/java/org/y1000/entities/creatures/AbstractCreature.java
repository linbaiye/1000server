package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventListener;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCreature implements Creature {

    private final long id;

    private Coordinate coordinate;

    private Direction direction;

    private final String name;

    private final List<EntityEventListener> eventListeners;

    public AbstractCreature(long id,
                            Coordinate coordinate,
                            Direction direction,
                            String name) {
        this.id = id;
        this.coordinate = coordinate;
        this.direction = direction;
        this.name = name;
        this.eventListeners = new ArrayList<>();
    }

    public void changeDirection(Direction newdir) {
        direction = newdir;
    }

    public void changeCoordinate(Coordinate newCoor) {
        coordinate = newCoor;
    }

    public void emitEvent(EntityEvent event) {
        eventListeners.forEach(listener -> listener.OnEvent(event));
    }

    void ClearListeners() {
        eventListeners.clear();
    }

    @Override
    public void registerOrderedEventListener(EntityEventListener listener) {
        eventListeners.add(listener);
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public Direction direction() {
        return direction;
    }

    @Override
    public String name() {
        return name;
    }
}
