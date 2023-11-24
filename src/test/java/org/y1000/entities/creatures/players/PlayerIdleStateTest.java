package org.y1000.entities.creatures.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.message.Message;
import org.y1000.message.MessageType;
import org.y1000.message.MoveMessage;
import org.y1000.message.PositionMessage;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlayerIdleStateTest {
    private PlayerImpl player;

    private PlayerIdleState state;

    private Realm realm;

    @BeforeEach
    void setUp() {
        player = Mockito.mock(PlayerImpl.class);
        state = PlayerIdleState.INSTANCE;
        realm = Mockito.mock(Realm.class);
    }

    @Test
    void moveIntoUnmovable() {
        when(player.coordinate()).thenReturn(new Coordinate(1, 1));
        when(player.direction()).thenReturn(Direction.DOWN);
        when(player.getRealm()).thenReturn(realm);
        when(player.id()).thenReturn(1L);
        when(realm.hasPhysicalEntityAt(any(Coordinate.class))).thenReturn(true);
        Optional<Message> ret = state.move(player, new MoveMessage(Direction.UP, player.coordinate(), player.id(), System.currentTimeMillis()));
        assertTrue(ret.isPresent());
        var msg = ret.get();
        assertEquals(MessageType.POSITION, msg.type());
        assertEquals(1, msg.sourceId());
        assertEquals(new Coordinate(1, 1), ((PositionMessage)msg).coordinate());
        assertEquals(((PositionMessage) msg).direction(), player.direction());
        verify(player, times(1)).changeDirection(Direction.UP);
    }

    @Test
    void move() {
        when(player.coordinate()).thenReturn(new Coordinate(1, 1));
        when(player.direction()).thenReturn(Direction.DOWN);
        when(player.getRealm()).thenReturn(realm);
        when(player.id()).thenReturn(1L);
        when(realm.hasPhysicalEntityAt(any(Coordinate.class))).thenReturn(false);
        Optional<Message> ret = state.move(player, new MoveMessage(player.direction(), player.coordinate(), player.id(), System.currentTimeMillis()));
        assertTrue(ret.isPresent());
        var msg = ret.get();
        assertEquals(MessageType.MOVE, msg.type());
        assertEquals(1, msg.sourceId());
        assertEquals(new Coordinate(1, 1), ((MoveMessage)msg).coordinate());
    }
}