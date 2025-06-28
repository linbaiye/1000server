package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.PlayerStateEnum;

import static org.junit.jupiter.api.Assertions.*;

class PlayerHurtPlayerStateTestEnum extends AbstractPlayerUnitTestFixture{

    private PlayerImpl player;

    @BeforeEach
    void setUp() {
        player = playerBuilder().build();
    }

    @Test
    void nestedHurt() {
        Player attacker = playerBuilder().build();
        while (!player.attackedBy(attacker));
        assertEquals(PlayerStateEnum.HURT, player.stateEnum());
        player.update(player.getStateMillis(PlayerStateEnum.HURT) - 10);
        assertEquals(PlayerStateEnum.HURT, player.stateEnum());
        // hurt again.
        while (!player.attackedBy(attacker));
        assertEquals(PlayerStateEnum.HURT, player.stateEnum());
        player.update(player.getStateMillis(PlayerStateEnum.HURT) );
        assertEquals(PlayerStateEnum.IDLE, player.stateEnum());
    }
}