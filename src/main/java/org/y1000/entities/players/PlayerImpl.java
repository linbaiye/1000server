package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.*;
import org.y1000.message.Interpolation;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
class PlayerImpl implements Player {

    private Coordinate coordinate;

    private Direction direction;

    private Realm realm;

    private PlayerState state;

    private final long id;

    PlayerImpl(long id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
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

    private List<ServerEvent> handleInputMessage(ClientEvent inputMessage) {
        if (!(inputMessage instanceof CharacterMovementEvent movementEvent)) {
            return Collections.emptyList();
        }
        log.debug("Handling message {}.", inputMessage);
        return state.handleMovementEvent(this, movementEvent);
//        return switch (movementEvent.inputMessage().type()) {
//            case MOUSE_RIGHT_CLICK -> state.onRightMouseClicked(this, (RightMouseClick) inputMessage);
//            case MOUSE_RIGHT_RELEASE -> state.onRightMouseReleased(this, (RightMouseRelease) inputMessage);
//            case MOUSE_RIGHT_MOTION -> state.OnRightMousePressedMotion(this, (RightMousePressedMotion) inputMessage);
//        };
    }

    public List<ServerEvent> handle(List<ClientEvent> messages) {
        List<ServerEvent> result = new ArrayList<>();
        for (ClientEvent message : messages) {
            result.addAll(handleInputMessage(message));
        }
        return result;
    }

    @Override
    public State state() {
        return state.getState();
    }

    @Override
    public void joinReam(Realm realm, long joinedAtMillis) {
        this.realm = realm;
        direction = Direction.DOWN;
        this.state = new PlayerIdleState();
    }

    @Override
    public PlayerInterpolation captureInterpolation() {
        Interpolation interpolation = new Interpolation(coordinate(), state(), direction(), state.elapsedMillis(), id());
        return new PlayerInterpolation(interpolation, true);
    }


    @Override
    public long id() {
        return id;
    }

    @Override
    public List<ServerEvent> update(long delta) {
        return state.update(this, delta);
    }

    boolean CanMoveOneUnit(Direction direction) {
        var nextCoordinate = coordinate().moveBy(direction);
        return realm.canMoveTo(nextCoordinate);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerImpl player = (PlayerImpl) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
