package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.*;
import org.y1000.message.input.InputMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
class PlayerImpl implements Player {

    private Coordinate coordinate;

    private Direction direction;

    private Realm realm;

    private PlayerState state;

    private long stateChangedAtMillis;

    private final long id;

    private long joinedAtMilli = -1;

    private long timeMillis;

    private final Deque<Interpolation> interpolations;


    PlayerImpl(long id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
        interpolations = new ArrayDeque<>();
    }

    void changeDirection(Direction newDirection) {
        direction = newDirection;
    }

    Realm getRealm() {
        return realm;
    }

    void changeState(PlayerState newState) {
        stateChangedAtMillis = timeMillis;
        state = newState;
    }

    void changeCoordinate(Coordinate newCoordinate) {
        coordinate = newCoordinate;
    }

    private List<I2ClientMessage> handleInputMessage(InputMessage inputMessage) {
        return switch (inputMessage.type()) {
            case MOUSE_RIGHT_CLICK -> state.onRightMouseClicked(this, (RightMouseClick) inputMessage);
            case MOUSE_RIGHT_RELEASE -> state.onRightMouseReleased(this, (RightMouseRelease) inputMessage);
            default -> Collections.emptyList();
        };
    }

    public List<I2ClientMessage> handle(List<Message> messages, long timeMillis) {
        List<I2ClientMessage> result = new ArrayList<>();
        for (Message message : messages) {
            if (message instanceof InputMessage inputMessage) {
                result.addAll(handleInputMessage(inputMessage));
            }
        }
        return result;
    }

    public List<I2ClientMessage> handle(List<Message> messages) {
        List<I2ClientMessage> result = new ArrayList<>();
        for (Message message : messages) {
            if (message instanceof InputMessage inputMessage) {
                result.addAll(handleInputMessage(inputMessage));
            }
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
        this.joinedAtMilli = joinedAtMillis;
        direction = Direction.DOWN;
        this.state = PlayerIdleState.INSTANCE;
        stateChangedAtMillis = joinedAtMillis;
    }

    @Override
    public Interpolation snapshot() {
        return state.snapshot(this, stateChangedAtMillis);
    }

    @Override
    public long joinedAtMilli() {
        return joinedAtMilli;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public List<I2ClientMessage> update(long delta) {
        return state.update(this, delta);
    }

    @Override
    public List<I2ClientMessage> update(long delta, long timeMilli) {
        this.timeMillis = timeMilli + delta;
        List<I2ClientMessage> messages = state.update(this, delta);
        takeSnapshot();
        return messages;
    }

    private void takeSnapshot() {
        Interpolation snapshot = state.snapshot(this, stateChangedAtMillis);
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
