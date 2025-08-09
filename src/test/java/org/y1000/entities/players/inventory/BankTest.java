package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.item.ItemFactory;

import static org.junit.jupiter.api.Assertions.*;

class BankTest extends AbstractUnitTestFixture {

    private final ItemFactory itemFactory = createItemFactory();

    @Test
    void canPut() {
        Bank bank = Bank.open();
        assertFalse(bank.canAdd(1, itemFactory.createItem("长剑")));
        bank.unlock();
        assertFalse(bank.canAdd(0, itemFactory.createItem("长剑")));
        assertTrue(bank.canAdd(1, itemFactory.createItem("长剑")));
        assertTrue(bank.canAdd(10, itemFactory.createItem("长剑")));
        assertFalse(bank.canAdd(11, itemFactory.createItem("长剑")));
        bank.add(1, itemFactory.createItem("长剑"));
        assertFalse(bank.canAdd(1, itemFactory.createItem("长剑")));
        bank.unlock();
        bank.unlock();
        bank.unlock();
        assertFalse(bank.canAdd(41, itemFactory.createItem("长剑")));
        assertTrue(bank.canAdd(40, itemFactory.createItem("长剑")));
    }

    @Test
    void unlock() {
        Bank bank = Bank.open();
        bank.unlock();
        bank.unlock();
        bank.unlock();
        bank.unlock();
        bank.unlock();
        assertFalse(bank.canUnlock());
        assertEquals(40, bank.getUnlocked());
    }

    @Test
    void isSlotOpen() {
        Bank bank = Bank.open();
        assertFalse(bank.isSlotOpen(0));
        assertFalse(bank.isSlotOpen(1));
        bank.unlock();
        assertTrue(bank.isSlotOpen(10));
    }
}