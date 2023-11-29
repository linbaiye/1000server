package org.y1000.entities.creatures.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.message.Message;
import org.y1000.message.MoveMessage;
import org.y1000.message.PositionMessage;
import org.y1000.message.StopMoveMessage;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PlayerWalkStateTest {

    private PlayerImpl player;

    private PlayerWalkState walkState;

    private Realm realm;

    @BeforeEach
    void setUp() {
        realm = Mockito.mock(Realm.class);
        player = new PlayerImpl(realm, new Coordinate(1, 1));
        walkState = new PlayerWalkState();
    }

    @Test
    void keepMoving() {
        Optional<Message> message = walkState.update(player, 10);
        assertTrue(message.isEmpty());
        when(realm.canMoveTo(any(Coordinate.class))).thenReturn(true);
        message = walkState.update(player, PlayerWalkState.MILLIS_PER_UNIT - 10);
        MoveMessage moveMessage = (MoveMessage)message.get();
        assertEquals(player.direction(), moveMessage.direction());
        assertEquals(new Coordinate(1, 2), moveMessage.coordinate());
    }

    @Test
    void keepMovingBlocked() {
        // One unit moved then stop.
        when(realm.canMoveTo(any(Coordinate.class))).thenReturn(false);
        player.changeDirection(Direction.LEFT);
        Optional<Message> message = walkState.update(player, PlayerWalkState.MILLIS_PER_UNIT);
        PositionMessage positionMessage = (PositionMessage)message.get();
        assertEquals(player.direction(), positionMessage.direction());
        assertEquals(new Coordinate(0, 1), player.coordinate());
        assertEquals(player.coordinate(), positionMessage.coordinate());
    }


    @Test
    void stopMove() {
        StopMoveMessage clientMessage = new StopMoveMessage(player.direction(),  player.coordinate(), player.id(), System.currentTimeMillis());
        Optional<Message> message = walkState.stopMove(player, clientMessage);
        StopMoveMessage stopMoveMessage = (StopMoveMessage)message.get();
        assertEquals(player.coordinate(), stopMoveMessage.coordinate());
        assertEquals(player.direction(), stopMoveMessage.direction());
        message = walkState.stopMove(player, clientMessage);
        assertTrue(message.isEmpty());
        Optional<Message> optionalMessage = walkState.update(player, PlayerWalkState.MILLIS_PER_UNIT);
        PositionMessage positionMessage = (PositionMessage)optionalMessage.get();
        assertEquals(player.coordinate(), positionMessage.coordinate());
    }
}