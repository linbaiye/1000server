package org.y1000.entities.item;

import lombok.Builder;
import org.apache.commons.lang3.Validate;
import org.y1000.message.ValueEnum;

public final class StackItem extends AbstractItem {
    private int number;
    private static final int MAX_NUMBER = 100000000;

    @Builder
    public StackItem(long id, String name, int number) {
        super(id, name, ItemType.STACK);
        Validate.isTrue(number > 0, "number must > 0");
        this.number = number;
    }

    public int number() {
        return number;
    }

    public void decrease(int n) {
        if (n >= number) {
            number = 0;
        }
        number -= n;
    }

    public boolean stack(Item item) {
        if (canStack(item)) {
            number += ((StackItem)item).number;
            return true;
        }
        return false;
    }
    public boolean canStack(Item item) {
        return item instanceof StackItem && item.name().equals(name());
    }

}
