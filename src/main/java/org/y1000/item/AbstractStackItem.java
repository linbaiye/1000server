package org.y1000.item;

import org.apache.commons.lang3.Validate;

public abstract class AbstractStackItem extends AbstractItem {
    private long number;
    private static final long MAX_NUMBER = 100000000000L;

    public AbstractStackItem(String name, long number, ItemType type,
    String dropSound, String eventSound) {
        super(name, type, dropSound, eventSound);
        Validate.isTrue(number > 0, "number must > 0");
        this.number = number;
    }


    public boolean canSplit(int number) {
        return this.number >= number && number > 0;
    }


    public boolean increase(long n) {
        if (n < 0) {
            return false;
        }
        if (hasMoreCapacity(n)) {
            number += n;
        }
        return true;
    }


    public boolean hasMoreCapacity(long more) {
        return number + more <= MAX_NUMBER;
    }

    public long number() {
        return number;
    }

    public long decrease(long n) {
        if (n > 0) {
            number = number > n ? number - n : 0;
        }
        return number;
    }

}
