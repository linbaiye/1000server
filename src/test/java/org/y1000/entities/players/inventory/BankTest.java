package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.Test;
import org.y1000.item.AbstractItemUnitTestFixture;

import static org.junit.jupiter.api.Assertions.*;

class BankTest extends AbstractItemUnitTestFixture {

    @Test
    void canPut() {
        Bank bank = Bank.open();
        assertFalse(bank.canPut(1, itemFactory.createItem("长剑")));
        bank.unlock();
        assertTrue(bank.canPut(1, itemFactory.createItem("长剑")));
        assertTrue(bank.canPut(10, itemFactory.createItem("长剑")));
        assertFalse(bank.canPut(11, itemFactory.createItem("长剑")));
        bank.unlock();
        bank.unlock();
        bank.unlock();
        assertFalse(bank.canPut(41, itemFactory.createItem("长剑")));
        assertTrue(bank.canPut(40, itemFactory.createItem("长剑")));
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
}