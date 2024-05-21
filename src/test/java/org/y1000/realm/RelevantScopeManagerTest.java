package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractEntityTest;
import org.y1000.entities.PhysicalEntity;
import org.y1000.util.Coordinate;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RelevantScopeManagerTest extends AbstractEntityTest {
    private RelevantScopeManager relevantScopeManager;
    @BeforeEach
    void setUp() {
        relevantScopeManager = new RelevantScopeManager();
    }

    private void assertSetEquals(Set<PhysicalEntity> first, Set<PhysicalEntity> second) {
        Set<PhysicalEntity> filtered = first.stream().filter(second::contains).collect(Collectors.toSet());
        assertEquals(filtered.size(), second.size());
        assertEquals(filtered.size(), first.size());
    }

    @Test
    void add() {
        PhysicalEntity entity = mockEntity(new Coordinate(10, 10));
        relevantScopeManager.add(entity);
        PhysicalEntity entity1 = mockEntity(new Coordinate(10, 11));
        Set<PhysicalEntity> affected = relevantScopeManager.add(entity1);
        assertTrue(affected.contains(entity));
        PhysicalEntity entity2 = mockEntity(new Coordinate(10, 12));
        Set<PhysicalEntity> affected2 = relevantScopeManager.add(entity2);
        assertTrue(affected2.contains(entity1));
        assertTrue(affected2.contains(entity));
        PhysicalEntity entity3 = mockEntity(new Coordinate(entity.coordinate().x(), entity.coordinate().y() + RelevantScope.Y_RANGE + 1));
        Set<PhysicalEntity> affected3 = relevantScopeManager.add(entity3);
        assertFalse(affected3.contains(entity));
        assertTrue(affected3.contains(entity1));
    }

    @Test
    void update() {
        PhysicalEntity entity1 = mockEntity(new Coordinate(10, 10));
        relevantScopeManager.add(entity1);
        PhysicalEntity entity2 = mockEntity(new Coordinate(10, 11));
        relevantScopeManager.add(entity2);
        PhysicalEntity entity3 = mockEntity(new Coordinate(10, 11 + RelevantScope.Y_RANGE + 2));
        relevantScopeManager.add(entity3);
        Set<PhysicalEntity> affected = relevantScopeManager.update(entity1);
        assertTrue(affected.isEmpty());
        when(entity1.coordinate()).thenReturn(new Coordinate(10, 11 + RelevantScope.Y_RANGE + 1 ));
        affected = relevantScopeManager.update(entity1);
        assertTrue(affected.contains(entity2));
        assertTrue(affected.contains(entity3));
    }
}