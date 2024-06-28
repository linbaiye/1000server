package org.y1000.entities.creatures.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;

import static org.junit.jupiter.api.Assertions.*;

class NpcMoveEventTest extends AbstractMonsterUnitTestFixture  {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void toPacket() {
        var event = NpcMoveEvent.move(monster, Direction.RIGHT, 2);
        assertEquals(2, event.toPacket().getMonsterMove().getSpeed());
        assertEquals(Direction.RIGHT.value(), event.toPacket().getMonsterMove().getDirection());
        assertEquals(monster.id(), event.toPacket().getMonsterMove().getId());
    }
}