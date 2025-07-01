package org.y1000.entities.creatures.monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.OldPlayerStateEnum;
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
        assertEquals(OldPlayerStateEnum.IDLE, monster.oldStateEnum());
        assertInstanceOf(NpcCommonState.class, monster.npcState());
    }
}