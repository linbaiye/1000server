package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.PlayerStateEnum;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDeadPlayerStateTestEnum extends AbstractPlayerUnitTestFixture {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void revive() {
        PlayerDeadState die = PlayerDeadState.die(player);
        player.changeState(die);
        player.update(die.totalMillis());
        assertSame(player.stateEnum(), PlayerStateEnum.IDLE);
    }
}