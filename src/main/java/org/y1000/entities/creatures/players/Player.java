package org.y1000.entities.creatures.players;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.message.*;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;
import org.y1000.realm.RealmMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Player implements Creature {

    private Coordinate myCoordinate;

    private long lastUpdate;

    private Direction direction;
    private final Realm realm;

    private PlayerState state;

    public Player(Realm realm) {
        myCoordinate = new Coordinate(36, 41);
        direction = Direction.DOWN;
        this.realm = realm;
        this.state = new PlayerIdleState();
    }
    void changeDirection(Direction newDirection) {
        direction = newDirection;
    }

    Realm getRealm() {
        return realm;
    }

    void changeState(PlayerState newState) {
        state = newState;
    }

    void changeCoordinate(Coordinate newCoordinate) {
        myCoordinate = newCoordinate;
    }

    boolean canMoveTo(Coordinate next) {
        return realm.map().movable(next) && !realm.hasPhysicalEntityAt(next);
    }

    public List<Message> handle(List<Message> messages) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message instanceof MoveMessage moveMessage) {
                result.add(state.move(this, moveMessage));
            }
        }
        return result;
    }

    @Override
    public long id() {
        return 0;
    }

    @Override
    public Optional<Message> update(long delta) {
        return state.update(this, delta);
    }

    @Override
    public Coordinate coordinate() {
        return myCoordinate;
    }
}
