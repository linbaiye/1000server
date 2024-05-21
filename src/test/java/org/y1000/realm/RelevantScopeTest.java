package org.y1000.realm;

import org.junit.jupiter.api.Test;
import org.y1000.AbstractEntityTest;
import org.y1000.entities.PhysicalEntity;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RelevantScopeTest extends AbstractEntityTest {

    @Test
    void outOfScope() {
        PhysicalEntity entity = mockEntity(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity, 1, 1);
        assertTrue(relevantScope.outOfScope(mockEntity(new Coordinate(2, 2))));
        assertFalse(relevantScope.outOfScope(mockEntity(new Coordinate(1, 1))));
    }

    @Test
    void addIfVisible() {
        PhysicalEntity entity = mockEntity(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity, 1, 1);
        assertFalse(relevantScope.addIfVisible(mockEntity(new Coordinate(2, 2))));
        PhysicalEntity another = mockEntity(new Coordinate(1, 1));
        assertTrue(relevantScope.addIfVisible(another));
        assertFalse(relevantScope.addIfVisible(another));
    }

    @Test
    void removeIfNotVisible() {
        PhysicalEntity entity = mockEntity(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity, 2, 2);
        assertFalse(relevantScope.removeIfNotVisible(mockEntity(new Coordinate(2, 2))));
        PhysicalEntity entity1 = mockEntity(new Coordinate(1, 2));
        relevantScope.addIfVisible(entity1);
        when(entity1.coordinate()).thenReturn(new Coordinate(3, 3));
        assertTrue(relevantScope.removeIfNotVisible(entity1));
    }

    @Test
    void update() {
        PhysicalEntity entity = mockEntity(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity, 2, 2);
        PhysicalEntity entity1 = mockEntity(new Coordinate(1, 2));
        relevantScope.addIfVisible(entity1);
        PhysicalEntity entity2 = mockEntity(new Coordinate(2, 2));
        relevantScope.addIfVisible(entity2);
        assertEquals(2, relevantScope.filter(PhysicalEntity.class).size());
        when(entity2.coordinate()).thenReturn(new Coordinate(2, 3));
        Set<PhysicalEntity> removed = relevantScope.update();
        assertTrue(removed.contains(entity2));
        assertEquals(1, relevantScope.filter(PhysicalEntity.class).size());
    }
}