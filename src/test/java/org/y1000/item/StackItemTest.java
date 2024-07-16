package org.y1000.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class StackItemTest {

    @Test
    void hasEnough() {
        StackItem stackItem = new StackItem(Mockito.mock(Item.class), 20);
        assertTrue(stackItem.hasEnough(19));
        assertTrue(stackItem.hasEnough(20));
        assertFalse(stackItem.hasEnough(21));
        assertTrue(stackItem.hasEnough(0));
        assertTrue(stackItem.hasEnough(-1));
    }

    @Test
    void decrease() {
        StackItem stackItem = new StackItem(Mockito.mock(Item.class), 2);
        var decreased = stackItem.decrease(2);
        assertEquals(0, decreased.number());
        decreased = stackItem.decrease(1);
        assertEquals(1, decreased.number());
        decreased = stackItem.decrease(1);
        assertEquals(1, decreased.number());
        decreased = stackItem.decrease(20);
        assertEquals(0, decreased.number());
    }

    @Test
    void increase() {
        StackItem stackItem = new StackItem(Mockito.mock(Item.class), 2);
        var increased = stackItem.increase(2);
        assertEquals(4, increased.number());
        increased = stackItem.increase(StackItem.capacity());
        assertEquals(2, increased.number());
    }

    @Test
    void hasMoreSpace() {
        StackItem stackItem = new StackItem(Mockito.mock(Item.class), StackItem.capacity() -1);
        assertTrue(stackItem.hasMoreSpace(1));
        assertFalse(stackItem.hasMoreSpace(2));
    }
}