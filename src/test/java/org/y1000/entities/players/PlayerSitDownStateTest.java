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
    void standOrSit() {
        withMockPlayer();
        state.update(PlayerSitDownState.StateMillis);
        state.sitOrStandUp();
        assertNotNull(eventListener.removeFirst(PlayerChangeStateEvent.class));
        verify(player, times(1)).changeState(any(PlayerStandUpState.class));
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
    void toggleFootKungFu() {
        withMockPlayer();
        var mock = mock(FootKungFu.class);
        state.update(PlayerSitDownState.StateMillis);
        state.tryToggleFootKungFu(mock);
        verify(player, times(1)).toggleFootKungFu(mock);
        assertNotNull(eventListener.removeFirst(PlayerChangeStateEvent.class));
        verify(player, times(1)).changeState(any(PlayerStandUpState.class));
    }

    @Test
    void toggleBreathKungFu() {
        withMockPlayer();
        var mock = mock(BreathKungFu.class);
        state.update(PlayerSitDownState.StateMillis);
        state.tryToggleBreathKungFu(mock);
        verify(player, times(1)).toggleBreathKungFu(mock);
        assertTrue(eventListener.isEmpty());
    }

    @Test
    void acceptAttack() {
        withMockPlayer();
        var entity = Mockito.mock(ActiveEntity.class);
        state.attack(entity);
        verify(player, times(0)).tryAcceptAttack(entity);

        state.update(PlayerSitDownState.StateMillis);
        when(player.tryAcceptAttack(entity)).thenReturn(-1);
        state.attack(entity);
        verify(player, times(0)).changeState(any(PlayerState.class));


        doAnswer(i -> {
            assertTrue(i.getArgument(0) instanceof PlayerStandUpState);
            return null;
        }).when(player).changeState(any(PlayerStandState.class));
        when(player.tryAcceptAttack(entity)).thenReturn(0);
        state.attack(entity);
        verify(player, times(1)).changeState(any(PlayerState.class));
    }
}