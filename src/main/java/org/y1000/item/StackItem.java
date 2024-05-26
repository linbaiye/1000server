package org.y1000.item;

import lombok.Builder;
import org.apache.commons.lang3.Validate;


public final class StackItem extends AbstractItem {

    private int number;
    private static final int MAX_NUMBER = 100000000;

    @Builder
    public StackItem(String name, int number, ItemType type) {
        super(name, type);
        Validate.isTrue(number > 0, "number must > 0");
        this.number = number;
    }

    public boolean increase(int n) {
        if (n < 0) {
            return false;
        }
        if (hasMoreCapacity(n)) {
            number += n;
        }
        return true;
    }

    public boolean canMerge(Item item) {
        return item instanceof StackItem stackItem && item.name().equals(name())
                && stackItem.hasMoreCapacity(stackItem.number());
    }


    public boolean hasMoreCapacity(int more) {
        return number + more <= MAX_NUMBER;
    }

    public int number() {
        return number;
    }

    public void drop(int n) {
        if (n <= 0) {
            return;
        }
        number = number > n ? number - n : 0;
    }
}
