package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.State;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDeadStateTest extends AbstractPlayerUnitTestFixture {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void revive() {
        PlayerDeadState die = PlayerDeadState.die(player);
        player.changeState(die);
        player.update(die.totalMillis());
        assertSame(player.stateEnum(), State.IDLE);
    }
}