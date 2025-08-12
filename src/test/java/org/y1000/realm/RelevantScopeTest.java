package org.y1000.realm;

import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.npc.Npc;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RelevantScopeTest extends AbstractUnitTestFixture {

    private Npc createNpc(Coordinate coordinate) {
        return npcFactory.create(mockRealmMap(), coordinate);
    }

    private Npc createNpc(int x, int y) {
        return createNpc(new Coordinate(x, y));
    }


    @Test
    void outOfScope() {
        Entity entity = createNpc(0, 0);
        RelevantScope relevantScope = new RelevantScope(entity);
        Entity another = createNpc(16, 16);
        assertTrue(relevantScope.outOfScope(another));
        another = createNpc(15, 13);
        assertFalse(relevantScope.outOfScope(another));
    }

    @Test
    void addIfVisible() {
        Entity entity = createNpc(0, 0);
        RelevantScope relevantScope = new RelevantScope(entity);
        assertFalse(relevantScope.addIfVisible(createNpc(new Coordinate(16, 16))));
        Entity another = createNpc(new Coordinate(15, 13));
        assertTrue(relevantScope.addIfVisible(another));
        assertFalse(relevantScope.addIfVisible(another));
    }

    @Test
    void removeIfNotVisible() {
        Entity entity = createNpc(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity);
        Npc entity1 = createNpc(new Coordinate(1, 2));
        relevantScope.addIfVisible(entity1);
        entity1.changeCoordinate(new Coordinate(15, 16));
        assertTrue(relevantScope.removeIfNotVisible(entity1));
    }

    @Test
    void update() {
        Entity entity = createNpc(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity);
        Entity entity1 = createNpc(new Coordinate(1, 2));
        relevantScope.addIfVisible(entity1);
        Npc entity2 = createNpc(new Coordinate(2, 2));
        relevantScope.addIfVisible(entity2);
        assertEquals(2, relevantScope.filter(ActiveEntity.class).size());
        entity2.changeCoordinate(new Coordinate(16, 16));
        Set<Entity> removed = relevantScope.update();
        assertTrue(removed.contains(entity2));
        assertEquals(1, relevantScope.filter(ActiveEntity.class).size());
    }
}