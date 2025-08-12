package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.npc.Npc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RespawningEntityManagerTest {

    private EntityTimerManager<Npc> manager;


    @BeforeEach
    void setUp() {
        manager = new EntityTimerManager<>();
    }

    @Test
    void update() {
        var npc = Mockito.mock(Npc.class);
        manager.add(npc, 100);
        Set<Npc> update = manager.update(10);
        assertTrue(update.isEmpty());
        update = manager.update(90);
        assertTrue(update.contains(npc));
    }
}