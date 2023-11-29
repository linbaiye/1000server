package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.util.Coordinate;

import java.util.Arrays;

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
}