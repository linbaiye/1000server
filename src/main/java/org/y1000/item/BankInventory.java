package org.y1000.item;

public final class BankInventory extends AbstractStackItem {
    public BankInventory(String name, long number, ItemSdb itemSdb) {
        super(name, ItemType.BANK_INVENTORY, number, itemSdb);
    }
}
