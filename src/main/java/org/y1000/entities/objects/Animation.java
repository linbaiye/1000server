package org.y1000.entities.objects;

import lombok.Getter;

@Getter
public class Animation {
    private final int start;
    private final int end;
    private final boolean loop;
    private int elapsed;
    public final static int StepMillis = 200;
    private final int endMillis;

    private final int id;

    public Animation(int start, int end, boolean loop, int id) {
        this.start = start;
        this.end = end;
        this.loop = loop;
        this.id = id;
        endMillis = start != end ? end * StepMillis : 0;
        elapsed = 0;
    }

    public boolean elapse(int millis) {
        if (endMillis == 0)
            return false;
        if (elapsed + millis < endMillis) {
            elapsed += millis;
            return false;
        }
        if (loop)
            elapsed = 0;
        return true;
    }
}
