package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;
import org.y1000.entities.players.event.PlayerMovedEvent;
import org.y1000.entities.players.event.PlayerSetPositionAndStateEvent;
import org.y1000.message.PlayerChangeStateEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.util.Coordinate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PlayerMoveStateTest extends AbstractPlayerUnitTestFixture {

    private PlayerMoveState state;

    private MoveInput current;


    @BeforeEach
    void setUp() {
        super.setup();
        current = new MoveInput(player.coordinate(), Direction.DOWN);
        state = PlayerMoveState.noneFightMove(player, current);
    }

    @Test
    void updateWalk() {
        when(mockedRealm.map().movable(any(Coordinate.class))).thenReturn(true);
        state.update(MoveAction.Walk.getMillis());
        assertEquals(player.stateEnum(), PlayerStateEnum.Idle);
        assertNotNull(eventListener.findFirst(PlayerMovedEvent.class));
        PlayerChangeStateEvent event = eventListener.findFirst(PlayerChangeStateEvent.class).get();
        assertFalse(event.isIncludeSelf());
        var packet = event.toPacket().getPlayerChangeState();
        assertEquals(PlayerStateEnum.Idle.value(), packet.getState());
        assertEquals(player.coordinate(), current.destination());
    }

    @Test
    void updateWalk_whenDestinationNotMovable() {
        var old = player.coordinate();
        when(mockedRealm.map().movable(any(Coordinate.class))).thenReturn(false);
        state.update(MoveAction.Walk.getMillis());
        assertEquals(player.stateEnum(), PlayerStateEnum.Idle);
        assertNotNull(eventListener.findFirst(PlayerSetPositionAndStateEvent.class));
        assertEquals(player.coordinate(), old);
    }
}