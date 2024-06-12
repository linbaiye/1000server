package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

import java.io.Serializable;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RealmMapV2ImplTest extends AbstractUnitTestFixture {
    private final transient String name = "test";

    private RealmMap realmMap;
    byte[][] cells = new byte[10][10];

    @BeforeEach
    void setUp() {
        for (byte[] cell : cells) {
            Arrays.fill(cell, (byte) 0);
        }
        realmMap = new RealmMapV2Impl(cells, name);
    }

    @Test
    void movable() {
        cells[2][2] = 0x1;
        assertFalse(realmMap.movable(new Coordinate(2, 2)));
        //corner
        assertTrue(realmMap.movable(new Coordinate(0, 0)));
        // out of boundary.
        assertFalse(realmMap.movable(new Coordinate(13, 13)));
    }

    @Test
    void occupy() {
        PassiveMonster monster = monsterBuilder().coordinate(Coordinate.xy(1, 1)).build();
        assertTrue(realmMap.movable(monster.coordinate()));
        realmMap.occupy(monster);
        assertFalse(realmMap.movable(monster.coordinate()));
        realmMap.occupy(monster);
        assertFalse(realmMap.movable(monster.coordinate()));
        assertThrows(IllegalArgumentException.class, () -> realmMap.occupy(monsterBuilder().coordinate(Coordinate.xy(100, 100)).build()));
    }

    @Test
    void free() {
        PassiveMonster monster = monsterBuilder().coordinate(Coordinate.xy(1, 1)).build();
        realmMap.free(monster);
        assertTrue(realmMap.movable(monster.coordinate()));
        realmMap.occupy(monster);
        assertFalse(realmMap.movable(monster.coordinate()));
        realmMap.free(monster);
        realmMap.free(monster);
        assertTrue(realmMap.movable(monster.coordinate()));
    }

    @Test
    void startMap() {
        RealmMap start = RealmMap.Load("start").orElseThrow(IllegalArgumentException::new);
        assertFalse(start.movable(Coordinate.xy(1, 1)));
        assertTrue(start.movable(Coordinate.xy(47, 38)));
        assertTrue(start.movable(Coordinate.xy(44, 31)));
        assertFalse(start.movable(Coordinate.xy(45, 31)));
    }
}