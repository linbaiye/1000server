package org.y1000.realm;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GridAOIManagerTest {

    private int width;
    private int height;
    private GridAOIManager manager;

    private void build(int w, int h) {
        manager = new GridAOIManager(w, h);
        width = w;
        height = h;
    }

    private Entity mockEntity(int x, int y) {
        var entity = Mockito.mock(Entity.class);
        when(entity.coordinate()).thenReturn(Coordinate.xy(x, y));
        return entity;
    }

    @Test
    void add() {
        build(2, 2);
        Entity e1 = mockEntity(1, 1);
        assertTrue(manager.add(e1).isEmpty());
        Entity e2 = mockEntity(0, 0);
        assertEquals(1, manager.add(e2).size());
        assertEquals(1, manager.add(e2).size());
        Entity e3 = mockEntity(0, 0);
        assertEquals(2, manager.add(e3).size());
        when(e2.coordinate()).thenReturn(Coordinate.xy(0, 1));
        Set<Entity> visible = manager.add(e2);
        assertEquals(2, visible.size());
        assertTrue(visible.contains(e3));
        assertTrue(visible.contains(e1));
        assertTrue(manager.contains(e1));
        assertTrue(manager.contains(e2));
        assertTrue(manager.contains(e3));
        Entity e4 = mockEntity(1, 1);
        assertTrue(manager.add(e4).contains(e1));
        assertTrue(manager.add(e4).contains(e2));
        assertTrue(manager.add(e4).contains(e3));


        build(Entity.VISIBLE_X_RANGE * 2, Entity.VISIBLE_Y_RANGE * 2);
        e1 = mockEntity(0, 0);
        manager.add(e1);
        e2 = mockEntity(0, Entity.VISIBLE_Y_RANGE);
        assertTrue(manager.add(e2).contains(e1));
        when(e2.coordinate()).thenReturn(Coordinate.xy(0, Entity.VISIBLE_Y_RANGE+ 1));
        assertTrue(manager.add(e2).isEmpty());
        when(e2.coordinate()).thenReturn(Coordinate.xy(Entity.VISIBLE_X_RANGE, Entity.VISIBLE_Y_RANGE));
        assertTrue(manager.add(e2).contains(e1));
    }

    @Test
    void contains() {
        build(2, 2);
        Entity e1 = mockEntity(1, 1);
        assertFalse(manager.contains(e1));
        Entity e2 = mockEntity(1, 1);
        manager.add(e1);
        manager.add(e2);
        assertTrue(manager.contains(e1));
        assertTrue(manager.contains(e2));
    }

    @Test
    void update() {
        build(100, 100);
        var e1 = mockEntity(10, 10);
        manager.add(e1);
        var e2 = mockEntity(10, 11);
        manager.add(e2);
        var e3 = mockEntity(10, 11 + Entity.VISIBLE_Y_RANGE + 2);
        manager.add(e3);
        var e4 = mockEntity(10, Entity.VISIBLE_Y_RANGE);
        manager.add(e4);

        Set<Entity> affected = manager.update(e1);
        assertTrue(affected.isEmpty());
        when(e1.coordinate()).thenReturn(Coordinate.xy(10, 11 + Entity.VISIBLE_Y_RANGE + 1));
        affected = manager.update(e1);
        assertTrue(affected.contains(e2));
        assertTrue(affected.contains(e3));
        assertFalse(affected.contains(e4));
    }

    @Test
    void remove() {
        build(Entity.VISIBLE_X_RANGE * 2, Entity.VISIBLE_Y_RANGE * 2);
        Entity e1 = mockEntity(1, 1);
        assertFalse(manager.contains(e1));
    }
}