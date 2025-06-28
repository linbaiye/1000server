package org.y1000.entities.players.fight;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.players.PlayerImpl;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCooldownPlayerStateTestEnum extends AbstractUnitTestFixture {

    private PlayerImpl player;

    private TestingEventListener eventListener;

    @BeforeEach
    void setUp() {
        player = playerBuilder().build();
        eventListener = new TestingEventListener();
        player.registerEventListener(eventListener);
    }

    @Test
    void doables() {
        var state = new PlayerCooldownState(player.getStateMillis(PlayerStateEnum.FightStand));
        assertTrue(state.canSitDown());
        assertFalse(state.canStandUp());
        assertTrue(state.canUseFootKungFu());
        assertTrue(state.attackable());
    }

    @Test
    void hurtRelated() {
        var state = new PlayerCooldownState(player.getStateMillis(PlayerStateEnum.FightStand));
        assertSame(PlayerStateEnum.FightStand, state.decideAfterHurtState());
        var before = player.coordinate();
        state.moveToHurtCoordinate(player);
        assertEquals(before, player.coordinate());
        state.afterHurt(player);
        assertEquals(PlayerStateEnum.FightStand, player.stateEnum());
    }
}