package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.*;
import org.y1000.message.input.InputMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
class PlayerImpl implements Player {

    private Coordinate coordinate;

    private Direction direction;

    private final Realm realm;

    private PlayerState state;

    private final long id;

    PlayerImpl(Realm realm) {
        this(realm, new Coordinate(36, 41));
    }

    PlayerImpl(Realm realm, Coordinate coordinate) {
        this.coordinate = coordinate;
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

    private Optional<I2ClientMessage> handleInputMessage(InputMessage inputMessage) {
        return switch (inputMessage.type()) {
            case MOUSE_RIGHT_CLICK -> state.onRightMouseClicked(this, (RightMouseClick) inputMessage);
            case MOUSE_RIGHT_RELEASE -> state.onRightMouseReleased(this, (RightMouseRelease) inputMessage);
            default -> Optional.empty();
        };
    }


    public List<I2ClientMessage> handle(List<Message> messages) {
        List<I2ClientMessage> result = new ArrayList<>();
        for (Message message : messages) {
            Optional<I2ClientMessage> ret = Optional.empty();
            if (message instanceof InputMessage inputMessage) {
                ret = handleInputMessage(inputMessage);
            }
            ret.ifPresent(result::add);
        }
        return result;
    }

    @Override
    public State state() {
        return state.getState();
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public Optional<I2ClientMessage> update(long delta) {
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
