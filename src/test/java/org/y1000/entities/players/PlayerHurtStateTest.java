package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.equipment.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.event.PlayerChangeStateEvent;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


class PlayerHurtStateTest extends AbstractPlayerUnitTestFixture {
    private PlayerHurtState state;

    private PlayerState interruptedState;

    @BeforeEach
    void setUp() {
        super.setup();
        interruptedState = Mockito.mock(PlayerState.class);
        state = PlayerHurtState.create(player, interruptedState);
    }

    private void withMockPlayer() {
        mockPlayer();
        state = PlayerHurtState.create(player, interruptedState);
    }

    @Test
    void handleAfterHurt() {
        state.handleAfterHurt();
        verify(interruptedState, times(1)).handleAfterHurt();
    }

    @Test
    void equip() {
        withMockPlayer();
        Equipment mock = mock(Equipment.class);
        state.equip(1, mock);
        verify(player, times(1)).tryEquipFromSlot(1, mock);
    }

    @Test
    void update() {
        state.update(PlayerHurtState.StateMillis);
        verify(interruptedState, times(1)).handleAfterHurt();
    }

    @Test
    void acceptAttack() {
        withMockPlayer();
        var entity = Mockito.mock(ActiveEntity.class);
        state.attack(entity);
        verify(player, times(1)).tryAcceptAttack(entity);
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
        FootKungFu unnamedFoot = player.kungFuBook().getUnnamedFoot();
        withMockPlayer();
        when(player.stateEnum()).thenReturn(PlayerStateEnum.Idle);
        var mock = mock(FootKungFu.class);
        when(player.footKungFu()).thenReturn(Optional.of(unnamedFoot));
        state.tryToggleFootKungFu(mock);
        verify(player, times(1)).toggleFootAndSync(mock);
        verify(player, times(1)).stopCombat();
        verify(player, times(1)).changeState(any(PlayerStandState.class));
    }

    @Test
    void updateInterruptedHurtState() {
        PlayerStandState idle = PlayerStandState.idle(player);
        interruptedState = PlayerHurtState.create(player, idle);
        var s = PlayerHurtState.create(player, interruptedState);
        s.update(PlayerHurtState.StateMillis);
        assertEquals(PlayerStateEnum.Idle, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }
}