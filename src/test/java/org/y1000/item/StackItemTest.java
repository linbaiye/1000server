package org.y1000.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.event.OpenTradeWindowEvent;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StackItemTest extends AbstractUnitTestFixture {

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

    @Test
    void sounds() {
        Item mocked = Mockito.mock(Item.class);
        when(mocked.eventSound()).thenReturn(Optional.of("event"));
        when(mocked.dropSound()).thenReturn(Optional.of("drop"));
        StackItem stackItem = new StackItem(mocked, StackItem.capacity() -1);
        assertEquals("drop", stackItem.dropSound().get());
        assertEquals("event", stackItem.eventSound().get());
    }

    @Test
    void origin() {
        ItemFactory itemFactory = createItemFactory();
        var stackItem = (StackItem) itemFactory.createItem("蓝色染剂", 1);
        assertTrue(stackItem.origin(Dye.class).isPresent());
    }
}