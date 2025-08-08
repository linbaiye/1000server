package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.players.event.PlayerMoveEvent;
import org.y1000.entities.players.event.PlayerMovedEvent;
import org.y1000.entities.players.event.PlayerSetPositionAndStateEvent;
import org.y1000.item.Equipment;
import org.y1000.entities.players.event.PlayerChangeStateEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.util.Coordinate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlayerMoveStateTest extends AbstractPlayerUnitTestFixture {

    private PlayerMoveState state;

    private MoveInput current;


    @BeforeEach
    void setUp() {
        super.setup();
        current = new MoveInput(player.coordinate(), Direction.DOWN);
        state = PlayerMoveState.noneFightMove(player, current);
    }

    private void changeToFightWalk() {
        state = PlayerMoveState.fightWalk(player, current);
    }

    @Test
    void walk_update() {
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
    void fightWalk_update() {
        changeToFightWalk();
        state.update(MoveAction.FightWalk.getMillis());
        assertEquals(player.stateEnum(), PlayerStateEnum.FightStand);
        assertEquals(current.destination(), player.coordinate());
    }

    @Test
    void walk_whenDestinationNotMovable() {
        var old = player.coordinate();
        when(mockedRealm.map().movable(any(Coordinate.class))).thenReturn(false);
        state.update(MoveAction.Walk.getMillis());
        assertEquals(player.stateEnum(), PlayerStateEnum.Idle);
        assertNotNull(eventListener.findFirst(PlayerSetPositionAndStateEvent.class));
        assertEquals(player.coordinate(), old);
    }

    @Test
    void walk_whenNewMoveInputArrivedAndCoordinateMismatch() {
        state.tryMove(current);
        state.update(MoveAction.Walk.getMillis());
        assertEquals(current.destination(), player.coordinate());
        assertNotNull(eventListener.remove(PlayerMovedEvent.class));
        assertEquals(PlayerStateEnum.Idle, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerSetPositionAndStateEvent.class));
    }

    @Test
    void walk_whenNewMoveInputArrived() {
        state.tryMove(new MoveInput(current.destination(), Direction.RIGHT));
        state.update(MoveAction.Walk.getMillis());
        assertEquals(current.destination(), player.coordinate());
        assertNotNull(eventListener.remove(PlayerMovedEvent.class));
        assertEquals(PlayerStateEnum.Move, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerMoveEvent.class));
    }

    @Test
    void equip() {
        mockPlayer();
        state = PlayerMoveState.noneFightMove(player, current);
        Equipment mock = Mockito.mock(Equipment.class);
        state.equip(1, mock);
        verify(player, times(1)).tryEquipFromSlot(1, mock);
    }

    @Test
    void handleAfterHurt() {
        state = PlayerMoveState.fightWalk(player, current);
        state.handleAfterHurt();
        assertEquals(current.destination(), player.coordinate());
        assertEquals(PlayerStateEnum.FightStand, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerSetPositionAndStateEvent.class));
    }
}