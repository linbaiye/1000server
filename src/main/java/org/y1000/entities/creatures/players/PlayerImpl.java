package org.y1000.entities.creatures.players;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.message.*;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class PlayerImpl implements Player {

    private Coordinate coordinate;

    private long lastUpdate;

    private Direction direction;

    private final Realm realm;

    private PlayerState state;

    private final long id;

    PlayerImpl(Realm realm) {
        coordinate = new Coordinate(36, 41);
        state = PlayerIdleState.INSTANCE;
        direction = Direction.DOWN;
        id = 0;
        this.realm = realm;
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
        coordinate = newCoordinate;
    }

    boolean canMoveTo(Coordinate next) {
        return realm.map().movable(next) && !realm.hasPhysicalEntityAt(next);
    }

    public List<Message> handle(List<Message> messages) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            Optional<Message> ret = Optional.empty();
            if (message instanceof MoveMessage moveMessage) {
                ret = state.move(this, moveMessage);
            } else if (message instanceof StopMoveMessage stopMoveMessage) {
                ret = state.stopMove(this, stopMoveMessage);
            }
            ret.ifPresent(result::add);
        }
        return result;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public Optional<Message> update(long delta) {
        return state.update(this, delta);
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public Direction direction() {
        return direction;
    }
}
