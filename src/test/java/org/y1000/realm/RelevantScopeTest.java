package org.y1000.realm;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractEntityTest;
import org.y1000.entities.Entity;
import org.y1000.realm.RelevantScope;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RelevantScopeTest extends AbstractEntityTest {

    @Test
    void outOfScope() {
        Entity entity = mockEntity(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity, 1, 1);
        assertTrue(relevantScope.outOfScope(mockEntity(new Coordinate(2, 2))));
        assertFalse(relevantScope.outOfScope(mockEntity(new Coordinate(1, 1))));
    }

    @Test
    void addIfVisible() {
        Entity entity = mockEntity(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity, 1, 1);
        assertFalse(relevantScope.addIfVisible(mockEntity(new Coordinate(2, 2))));
        Entity another = mockEntity(new Coordinate(1, 1));
        assertTrue(relevantScope.addIfVisible(another));
        assertFalse(relevantScope.addIfVisible(another));
    }

    @Test
    void removeIfNotVisible() {
        Entity entity = mockEntity(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity, 2, 2);
        assertFalse(relevantScope.removeIfNotVisible(mockEntity(new Coordinate(2, 2))));
        Entity entity1 = mockEntity(new Coordinate(1, 2));
        relevantScope.addIfVisible(entity1);
        when(entity1.coordinate()).thenReturn(new Coordinate(3, 3));
        assertTrue(relevantScope.removeIfNotVisible(entity1));
    }

    @Test
    void update() {
        Entity entity = mockEntity(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity, 2, 2);
        Entity entity1 = mockEntity(new Coordinate(1, 2));
        relevantScope.addIfVisible(entity1);
        Entity entity2 = mockEntity(new Coordinate(2, 2));
        relevantScope.addIfVisible(entity2);
        assertEquals(2, relevantScope.filter(Entity.class).size());
        when(entity2.coordinate()).thenReturn(new Coordinate(2, 3));
        Set<Entity> removed = relevantScope.update();
        assertTrue(removed.contains(entity2));
        assertEquals(1, relevantScope.filter(Entity.class).size());
    }
}