package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.State;

import static org.junit.jupiter.api.Assertions.*;

class PlayerHurtStateTest extends AbstractPlayerUnitTestFixture{

    private PlayerImpl player;

    @BeforeEach
    void setUp() {
        player = playerBuilder().build();
    }

    @Test
    void nestedHurt() {
        Player attacker = playerBuilder().build();
        while (!player.attackedBy(attacker));
        assertEquals(State.HURT, player.stateEnum());
        player.update(player.getStateMillis(State.HURT) - 10);
        assertEquals(State.HURT, player.stateEnum());
        // hurt again.
        while (!player.attackedBy(attacker));
        assertEquals(State.HURT, player.stateEnum());
        player.update(player.getStateMillis(State.HURT) );
        assertEquals(State.IDLE, player.stateEnum());
    }
}