package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.ActiveEntity;
import org.y1000.item.Equipment;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.PlayerChangeStateEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class PlayerAttackStateTest extends AbstractPlayerUnitTestFixture {


    private PlayerMeleeState state;

    @BeforeEach
    void setUp() {
        super.setup();
        state = new PlayerMeleeState(player, AttackAction.Punch);
    }

    private void withMockPlayer() {
        mockPlayer();
        state = new PlayerMeleeState(player, AttackAction.Punch);
    }

    @Test
    void update() {
        state.update(AttackAction.Punch.getMillis());
        assertEquals(PlayerStateEnum.FightStand, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));

        withMockPlayer();
        when(player.tryCombatStrike(anyInt())).thenReturn(true);
        state.update(AttackAction.Punch.getMillis());
        assertTrue(eventListener.isEmpty());
    }

    @Test
    void attack() {
        withMockPlayer();
        ActiveEntity mock = Mockito.mock(ActiveEntity.class);
        state.attack(mock);
        verify(player, times(1)).tryAcceptAttack(mock);
    }

    @Test
    void handleAfterAttack() {
        state.handleAfterHurt();
        assertEquals(PlayerStateEnum.FightStand, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }

    @Test
    void tryChangeAttackKungFu() {
        withMockPlayer();
        AttackKungFu mock = mock(AttackKungFu.class);
        when(player.stateEnum()).thenReturn(PlayerStateEnum.FightStand);
        when(player.tryChangeAttackKungFu(mock)).thenReturn(true);
        state.tryToggleAttackKungFu(mock);
        verify(player, times(1)).changeState(any(PlayerStandState.class));
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }


    @Test
    void equip() {
        AttackKungFu unnamedAttack = player.kungFuBook().findUnnamedAttack(AttackKungFuType.Fist);
        AttackKungFu unnamedSword = player.kungFuBook().findUnnamedAttack(AttackKungFuType.SWORD);
        withMockPlayer();
        when(player.attackKungFu()).thenReturn(unnamedAttack);
        doAnswer(a -> {
            when(player.attackKungFu()).thenReturn(unnamedSword);
            return true;
        }).when(player).tryEquipFromSlot(anyInt(), any());
        when(player.stateEnum()).thenReturn(PlayerStateEnum.FightStand);
        Equipment mock = mock(Equipment.class);
        state.equip(1, mock);
        verify(player, times(1)).changeState(any(PlayerStandState.class));
    }


    @Test
    void toggleBreathKungFu() {
        withMockPlayer();
        BreathKungFu mock = mock(BreathKungFu.class);
        when(player.stateEnum()).thenReturn(PlayerStateEnum.Sit);
        state.tryToggleBreathKungFu(mock);
        verify(player, times(1)).stopCombat();
        verify(player, times(1)).toggleBreathAndSync(mock);
        verify(player, times(1)).changeState(any(PlayerSitDownState.class));
    }
}