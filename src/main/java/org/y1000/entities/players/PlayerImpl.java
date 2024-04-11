package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.*;
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

    private final List<ServerEventListener> eventListeners;

    private final Queue<ClientEvent> eventQueue;

    PlayerImpl(long id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
        this.eventListeners = new ArrayList<>();
        eventQueue = new ArrayDeque<>();
    }

    void changeDirection(Direction newDirection) {
        direction = newDirection;
    }

    boolean hasClientEvent() {
        return !eventQueue.isEmpty();
    }

    ClientEvent takeClientEvent() {
        return eventQueue.poll();
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

    void emitEvent(EntityEvent event) {
        eventListeners.forEach(listener -> listener.OnEvent(event));
    }

    @Override
    public void addAll(List<ClientEvent> clientEvents) {
        eventQueue.addAll(clientEvents);
    }

    @Override
    public State state() {
        return state.getState();
    }

    @Override
    public void joinReam(Realm realm) {
        this.realm = realm;
        direction = Direction.DOWN;
        this.state = new PlayerIdleState();
        emitEvent(new LoginMessage(this, coordinate()));
    }

    @Override
    public PlayerInterpolation captureInterpolation() {
        return PlayerInterpolation.FromPlayer(this, state.elapsedMillis());
    }

    @Override
    public void registerListener(ServerEventListener listener) {
        eventListeners.add(listener);
    }


    @Override
    public long id() {
        return id;
    }

    @Override
    public void update(long delta) {
        state.update(this, delta);
    }


    boolean CanMoveOneUnit(Direction direction) {
        var nextCoordinate = coordinate().moveBy(direction);
        return realm.canMoveTo(nextCoordinate);
    }


    @Override
    public String toString() {
        return "PlayerImpl{" +
                "id=" + id +
                ", coordinate=" + coordinate +
                ", direction=" + direction +
                ", state=" + state.getState() +
                '}';
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
