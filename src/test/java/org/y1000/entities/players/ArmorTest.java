package org.y1000.entities.players;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArmorTest {

    @Test
    void add() {
        var result = new Armor(1, 2,3,4).add(null);
        assertEquals(1, result.body());
        assertEquals(2, result.head());
        assertEquals(3, result.arm());
        assertEquals(4, result.leg());
    }
}