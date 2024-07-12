package org.y1000.item;

import org.apache.commons.lang3.Validate;

public abstract class AbstractStackItem extends AbstractItem {
    private long number;
    private static final long MAX_NUMBER = 100000000000L;

    public AbstractStackItem(String name, long number, ItemType type,
                             String dropSound, String eventSound) {
        this(name, number, type, dropSound, eventSound, null);
    }

    public AbstractStackItem(String name, long number, ItemType type,
                             String dropSound, String eventSound, String desc) {
        super(name, type, dropSound, eventSound, desc);
        Validate.isTrue(number > 0, "number must > 0");
        this.number = number;
    }


    public void increase(long n) {
        if (n < 0) {
            return;
        }
        if (hasMoreCapacity(n)) {
            number += n;
        }
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
