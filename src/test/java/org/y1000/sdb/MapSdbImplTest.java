package org.y1000.sdb;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MapSdbImplTest {

    private final MapSdbImpl sdb = MapSdbImpl.INSTANCE;

    @Test
    void getMapName() {
        String mapName = sdb.getMapName(49);
        assertEquals("start.map", mapName);
    }
}