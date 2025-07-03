package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.npc.INpc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RespawningEntityManagerTest {

    private EntityTimerManager<INpc> manager;


    @BeforeEach
    void setUp() {
        manager = new EntityTimerManager<>();
    }

    @Test
    void update() {
        var npc = Mockito.mock(INpc.class);
        manager.add(npc, 100);
        Set<INpc> update = manager.update(10);
        assertTrue(update.isEmpty());
        update = manager.update(90);
        assertTrue(update.contains(npc));
    }
}