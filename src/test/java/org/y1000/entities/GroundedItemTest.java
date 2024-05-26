package org.y1000.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class GroundedItemTest {

    private GroundedItem item;

    @BeforeEach
    void setUp() {
        item = new GroundedItem(1, "test", new Coordinate(1, 2), 0, 0, 1);
    }

    @Test
    void canPickAt() {
        assertTrue(item.canPickAt(new Coordinate(1, 2)));
        assertFalse(item.canPickAt(new Coordinate(4, 2)));
        assertFalse(item.canPickAt(new Coordinate(1, 6)));
    }
}