package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractEntityTest;
import org.y1000.entities.Entity;
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

    private void assertSetEquals(Set<Entity> first, Set<Entity> second) {
        Set<Entity> filtered = first.stream().filter(second::contains).collect(Collectors.toSet());
        assertEquals(filtered.size(), second.size());
        assertEquals(filtered.size(), first.size());
    }

    @Test
    void add() {
        Entity entity = mockEntity(new Coordinate(10, 10));
        relevantScopeManager.add(entity);
        Entity entity1 = mockEntity(new Coordinate(10, 11));
        Set<Entity> affected = relevantScopeManager.add(entity1);
        assertTrue(affected.contains(entity));
        Entity entity2 = mockEntity(new Coordinate(10, 12));
        Set<Entity> affected2 = relevantScopeManager.add(entity2);
        assertTrue(affected2.contains(entity1));
        assertTrue(affected2.contains(entity));
        Entity entity3 = mockEntity(new Coordinate(entity.coordinate().x(), entity.coordinate().y() + RelevantScope.Y_RANGE + 1));
        Set<Entity> affected3 = relevantScopeManager.add(entity3);
        assertFalse(affected3.contains(entity));
        assertTrue(affected3.contains(entity1));
    }

    @Test
    void update() {
        Entity entity1 = mockEntity(new Coordinate(10, 10));
        relevantScopeManager.add(entity1);
        Entity entity2 = mockEntity(new Coordinate(10, 11));
        relevantScopeManager.add(entity2);
        Entity entity3 = mockEntity(new Coordinate(10, 11 + RelevantScope.Y_RANGE + 2));
        relevantScopeManager.add(entity3);
        Set<Entity> affected = relevantScopeManager.update(entity1);
        assertTrue(affected.isEmpty());
        when(entity1.coordinate()).thenReturn(new Coordinate(10, 11 + RelevantScope.Y_RANGE + 1 ));
        affected = relevantScopeManager.update(entity1);
        assertTrue(affected.contains(entity2));
        assertTrue(affected.contains(entity3));
    }
}