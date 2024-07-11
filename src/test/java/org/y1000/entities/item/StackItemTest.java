package org.y1000.entities.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.item.ItemSdbImpl;
import org.y1000.item.ItemType;
import org.y1000.item.DefaultStackItem;

import static org.junit.jupiter.api.Assertions.*;

class StackItemTest {

    private DefaultStackItem stackItem;

    @BeforeEach
    void setUp() {
        stackItem = new DefaultStackItem( "test", 15, ItemType.ARROW, ItemSdbImpl.INSTANCE);
    }

    @Test
    void drop() {
        stackItem.decrease(1);
        assertEquals(14, stackItem.number());
        stackItem.decrease(-10);
        assertEquals(14, stackItem.number());
        stackItem.decrease(0);
        assertEquals(14, stackItem.number());
        stackItem.decrease(15);
        assertEquals(0, stackItem.number());
        setUp();
        stackItem.decrease(15);
        assertEquals(0, stackItem.number());
    }

    @Test
    void type() {
    }
}