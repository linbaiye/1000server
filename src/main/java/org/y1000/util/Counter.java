package org.y1000.util;

public final class Counter {

    private final int total;
    private int count;

    public Counter(int total) {
        this.total = total;
        count = 0;
    }

    public boolean count(int n) {
        if (count >= total)
            return true;
        count += n;
        return count >= total;
    }

    public void reset() {
        count = 0;
    }
    public static Counter of(int t) {
        return new Counter(t);
    }
}
