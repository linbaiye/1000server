package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.util.Coordinate;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RealmMapV2ImplTest {

    private RealmMap realmMap;
    byte[][] cells = new byte[10][10];

    @BeforeEach
    void setUp() {
        for (byte[] cell : cells) {
            Arrays.fill(cell, (byte) 1);
        }
        realmMap = new RealmMapV2Impl(cells);
    }

    @Test
    void movable() {
        cells[2][2] = 0x4;
        assertTrue(realmMap.movable(new Coordinate(2, 2)));
        assertFalse(realmMap.movable(new Coordinate(0, 0)));
        assertFalse(realmMap.movable(new Coordinate(13, 13)));
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