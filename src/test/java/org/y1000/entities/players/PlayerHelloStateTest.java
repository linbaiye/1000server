package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.message.PlayerChangeStateEvent;

import static org.junit.jupiter.api.Assertions.*;

class PlayerHelloStateTest extends AbstractPlayerUnitTestFixture {

    private PlayerHelloState state;

    @BeforeEach
    void setUp() {
        super.setup();
        state = new PlayerHelloState(player);
    }


    @Test
    void update() {
        state.update(PlayerHelloState.StateMillis);
        assertEquals(PlayerStateEnum.Idle, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }

    @Test
    void handleAfterHurt() {
        state.handleAfterHurt();
        assertEquals(PlayerStateEnum.Idle, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }
}