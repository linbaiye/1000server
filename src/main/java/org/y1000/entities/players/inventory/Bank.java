package org.y1000.entities.players.inventory;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.item.Item;

@Getter
public final class Bank extends AbstractInventory {
    private int unlocked;

    private static final int MAXSIZE = 40;

    public Bank(int cap, int unlocked) {
        super(cap);
        Validate.isTrue(unlocked >= 0 && unlocked <= cap);
        this.unlocked = unlocked;
    }

    public int findAvailableSlot() {
        for (int i = 1; i <= unlocked; i++) {
            if (getItem(i) == null)
                return i;
        }
        return 0;
    }

    @Override
    public boolean isFull() {
        return unlocked == items().size();
    }

    public boolean canPut(int slot, Item item) {
        if (slot < 1 || slot > unlocked || item == null) {
            return false;
        }
        return canPut(item, slot);
    }
    public void add(int slot, Item item) {
        if (isSlotUnlocked(slot))
            doAdd(slot, item);
    }

    public void unlock() {
        if (canUnlock())
            unlocked += 10;
    }

    public boolean isSlotUnlocked(int slot) {
        return slot >= 1 && slot <= unlocked;
    }

    public boolean canUnlock() {
        return unlocked < capacity();
    }

    public static Bank open() {
        return new Bank(MAXSIZE, 0);
    }
}
