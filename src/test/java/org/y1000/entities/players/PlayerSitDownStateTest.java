package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.ActiveEntity;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.PlayerChangeStateEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlayerSitDownStateTest extends AbstractPlayerUnitTestFixture {

    private PlayerSitDownState state;

    @BeforeEach
    void setUp() {
        super.setup();
        state = new PlayerSitDownState(player);
    }

    private void withMockPlayer() {
        mockPlayer();
        state = new PlayerSitDownState(player);
        when(player.stateEnum()).thenReturn(PlayerStateEnum.Sit);
    }

    @Test
    void handleAfterHurt() {
        state.handleAfterHurt();
        assertNotNull(eventListener.removeFirst(PlayerChangeStateEvent.class));
        assertEquals(PlayerStateEnum.Sit, player.stateEnum());
    }

    @Test
    void standOrSit_notPastHalf() {
        withMockPlayer();
        doAnswer(a -> {
            assertEquals(PlayerStateEnum.Idle, a.getArgument(0, PlayerStandState.class).playerStateEnum());
            return null;
        }).when(player).changeState(any(PlayerStandState.class));
        state.sitOrStandUp();
        assertNotNull(eventListener.removeFirst(PlayerChangeStateEvent.class));
        verify(player, times(1)).changeState(any(PlayerStandState.class));
    }

    @Test
    void standOrSit_whenSat() {
        withMockPlayer();
        state.update(PlayerSitDownState.StateMillis);
        state.sitOrStandUp();
        assertNotNull(eventListener.removeFirst(PlayerChangeStateEvent.class));
        verify(player, times(1)).changeState(any(PlayerStandUpState.class));
        verify(player, times(1)).disableBreathAndSync();
    }

    @Test
    void equip() {
        withMockPlayer();
        Equipment mock = mock(Equipment.class);
        state.equip(1, mock);
        verify(player, times(1)).tryEquipFromSlot(1, mock);
    }

    @Test
    void toggleAttackKungFu() {
        withMockPlayer();
        AttackKungFu mock = mock(AttackKungFu.class);
        state.tryToggleAttackKungFu(mock);
        verify(player, times(1)).tryChangeAttackKungFu(mock);
    }

    @Test
    void toggleFootKungFu_whenSat() {
        withMockPlayer();
        var mock = mock(FootKungFu.class);
        state.update(PlayerSitDownState.StateMillis);
        state.tryToggleFootKungFu(mock);
        verify(player, times(1)).toggleFootAndSync(mock);
        assertNotNull(eventListener.removeFirst(PlayerChangeStateEvent.class));
        verify(player, times(1)).changeState(any(PlayerStandUpState.class));
    }

    @Test
    void toggleBreathKungFu_whenSat() {
        withMockPlayer();
        var mock = mock(BreathKungFu.class);
        state.update(PlayerSitDownState.StateMillis);
        state.tryToggleBreathKungFu(mock);
        verify(player, times(1)).toggleBreathAndSync(mock);
        assertTrue(eventListener.isEmpty());
    }

    @Test
    void acceptAttack_notHalfTimePast() {
        withMockPlayer();
        var entity = Mockito.mock(ActiveEntity.class);
        when(player.tryAcceptAttack(entity)).thenReturn(0);
        state.attack(entity);
        verify(player, times(1)).tryAcceptAttack(entity);
        doAnswer(a -> {
            assertEquals(PlayerStateEnum.FightStand, a.getArgument(0, PlayerStandState.class).playerStateEnum());
            return null;
        }).when(player).changeState(any(PlayerStandState.class));
        verify(player, times(1)).changeState(any(PlayerStandState.class));
    }

    @Test
    void acceptAttack_pastHalfTime() {
        withMockPlayer();
        var entity = Mockito.mock(ActiveEntity.class);
        state.update(PlayerSitDownState.StateMillis);
        when(player.tryAcceptAttack(entity)).thenReturn(0);
        doAnswer(i -> {
            assertInstanceOf(PlayerStandUpState.class, i.getArgument(0));
            return null;
        }).when(player).changeState(any(PlayerStandState.class));
        state.attack(entity);
        verify(player, times(1)).changeState(any(PlayerState.class));
        verify(player, times(1)).disableBreathAndSync();
    }
}