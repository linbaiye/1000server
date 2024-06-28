package org.y1000.entities.creatures.monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.NpcChangeStateEvent;
import org.y1000.entities.creatures.npc.NpcCommonState;

import static org.junit.jupiter.api.Assertions.*;

class MonsterWanderingTest extends AbstractMonsterUnitTestFixture {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void start() {
        monster.start();
        assertNotNull(eventListener.removeFirst(NpcChangeStateEvent.class));
        assertEquals(State.IDLE, monster.stateEnum());
        assertInstanceOf(NpcCommonState.class, monster.state());
    }

    @Test
    void onIdleDone() {
        monster.start();
        eventListener.clearEvents();
        monster.update(monster.getStateMillis(State.IDLE));
        assertEquals(State.FROZEN, monster.stateEnum());
        assertInstanceOf(NpcCommonState.class, monster.state());
        assertNotNull(eventListener.removeFirst(NpcChangeStateEvent.class));
    }
}