package org.y1000.entities.objects;

public class Animation {
    private final int start;
    private final int end;
    private final boolean loop;
    private int elapsed;
    public final static int StepMillis = 200;
    private final int endMillis;

    public Animation(int start, int end, boolean loop) {
        this.start = start;
        this.end = end;
        this.loop = loop;
        endMillis = start != end ? end * StepMillis : 0;
        elapsed = 0;
    }

    public void elapse(int millis) {
        if (endMillis == 0)
            return;
        elapsed += millis;
        if (elapsed >= endMillis)
            elapsed = 0;
    }
}
