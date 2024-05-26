package org.y1000.entities.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.item.ItemType;
import org.y1000.item.StackItem;

import static org.junit.jupiter.api.Assertions.*;

class StackItemTest {

    private StackItem stackItem;

    @BeforeEach
    void setUp() {
        stackItem = new StackItem( "test", 15, ItemType.ARROW);
    }

    @Test
    void drop() {
        stackItem.drop(1);
        assertEquals(14, stackItem.number());
        stackItem.drop(-10);
        assertEquals(14, stackItem.number());
        stackItem.drop(0);
        assertEquals(14, stackItem.number());
        stackItem.drop(15);
        assertEquals(0, stackItem.number());
        setUp();
        stackItem.drop(15);
        assertEquals(0, stackItem.number());
    }

    @Test
    void type() {
    }
}