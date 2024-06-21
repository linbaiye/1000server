package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.State;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRevivalTest {

    private PlayerRevival revival;

    @BeforeEach
    void setUp() {
        revival = new PlayerRevival();
    }

    @Test
    void gainExp() {
        var newRevival = revival.gainExp();
        assertNotEquals(newRevival.level(), revival.level());
    }

    @Test
    void level() {
        assertEquals(100, revival.level());
    }

    @Test
    void regenerateHalLife() {
        assertEquals(303, revival.regenerateHalLife(State.DIE));
        assertEquals(151, revival.regenerateHalLife(State.SIT));
        assertEquals(80, revival.regenerateHalLife(State.IDLE));
        assertEquals(50, revival.regenerateHalLife(State.RUN));
    }

    @Test
    void regenerateResources() {
        assertEquals(101, revival.regenerateResources(State.DIE));
        assertEquals(70, revival.regenerateResources(State.SIT));
        assertEquals(50, revival.regenerateResources(State.IDLE));
        assertEquals(10, revival.regenerateResources(State.RUN));
    }
}