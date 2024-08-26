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

    public boolean canPut(int slot, Item item) {
        if (slot < 1 || slot > unlocked || item == null) {
            return false;
        }
        return canPut(item, slot);
    }

    public void unlock() {
        if (canUnlock())
            unlocked += 10;
    }

    public boolean canUnlock() {
        return unlocked < capacity();
    }

    public static Bank open() {
        return new Bank(MAXSIZE, 0);
    }
}
