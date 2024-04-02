package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.*;
import org.y1000.message.input.InputMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMousePressedMotion;
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

    // Milliseconds relative to joinedAtMillis the current state was changed at.
    private long stateChangedAtMillis;

    private final long id;

    private long joinedAtRealmMilli = -1;

    private final Deque<Interpolation> interpolations;

    private static final int INTERPOLATION_MAX_SIZE = 20;

    // Is in update method?
    private boolean updating;

    PlayerImpl(long id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
        interpolations = new ArrayDeque<>(INTERPOLATION_MAX_SIZE);
        updating = false;
    }

    void changeDirection(Direction newDirection) {
        direction = newDirection;
    }

    Realm getRealm() {
        return realm;
    }

    void changeState(PlayerState newState) {
        state = newState;
        stateChangedAtMillis = (joinedAtRealmMilli -  realm.timeMillis())
                + (updating ? realm.stepMillis() : 0);
    }

    void changeCoordinate(Coordinate newCoordinate) {
        coordinate = newCoordinate;
    }

    private List<I2ClientMessage> handleInputMessage(InputMessage inputMessage) {
        return switch (inputMessage.type()) {
            case MOUSE_RIGHT_CLICK -> state.onRightMouseClicked(this, (RightMouseClick) inputMessage);
            case MOUSE_RIGHT_RELEASE -> state.onRightMouseReleased(this, (RightMouseRelease) inputMessage);
            case MOUSE_RIGHT_MOTION -> state.OnRightMousePressedMotion(this, (RightMousePressedMotion) inputMessage);
        };
    }

    public List<I2ClientMessage> handle(List<InputMessage> messages) {
        List<I2ClientMessage> result = new ArrayList<>();
        for (InputMessage message : messages) {
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
        this.joinedAtRealmMilli = joinedAtMillis;
        direction = Direction.DOWN;
        this.state = new PlayerIdleState();
        stateChangedAtMillis = 0;
        interpolations.clear();
    }

    @Override
    public long interpolationDuration() {
        return interpolations.size() * realm.stepMillis();
    }

    @Override
    public List<Interpolation> drainInterpolations(long durationMillis) {
        if (interpolationDuration() < durationMillis) {
            return Collections.emptyList();
        }
        if (durationMillis >= INTERPOLATION_MAX_SIZE * realm.stepMillis()) {
            return Collections.emptyList();
        }
        List<Interpolation> result = new ArrayList<>();
        for (int i = 0; i < durationMillis / realm.stepMillis(); i++) {
            result.add(interpolations.pollFirst());
        }
        return result;
    }


    @Override
    public long joinedAtMilli() {
        return joinedAtRealmMilli;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public List<I2ClientMessage> update(long delta, long timeMilli) {
        updating = true;
        List<I2ClientMessage> messages = state.update(this, delta);
        captureInterpolation();
        updating = false;
        return messages;
    }

    private void captureInterpolation() {
        Interpolation snapshot = state.captureInterpolation(this, stateChangedAtMillis);
        if (snapshot == null) {
            return;
        }
        interpolations.add(snapshot);
        while (interpolations.size() >= INTERPOLATION_MAX_SIZE) {
            interpolations.remove();
        }
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
