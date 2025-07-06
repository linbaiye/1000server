package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.OldPlayerStateEnum;

import static org.junit.jupiter.api.Assertions.*;

class PlayerHurtPlayerStateTestEnum extends AbstractPlayerUnitTestFixture{

    private PlayerImpl player;

    @BeforeEach
    void setUp() {
        player = playerBuilder().build();
    }

    /*
    @Test
    void nestedHurt() {
        Player attacker = playerBuilder().build();
        while (!player.attackedBy(attacker));
        assertEquals(OldPlayerStateEnum.HURT, player.oldStateEnum());
        player.update(player.getStateMillis(OldPlayerStateEnum.HURT) - 10);
        assertEquals(OldPlayerStateEnum.HURT, player.oldStateEnum());
        // hurt again.
        while (!player.attackedBy(attacker));
        assertEquals(OldPlayerStateEnum.HURT, player.oldStateEnum());
        player.update(player.getStateMillis(OldPlayerStateEnum.HURT) );
        assertEquals(OldPlayerStateEnum.IDLE, player.oldStateEnum());
    }*/
}