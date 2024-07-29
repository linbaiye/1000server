package org.y1000.entities.objects;

import org.apache.commons.lang3.Validate;

public record Animation(int frameStart, int frameEnd, boolean loop) {
    public Animation {
        Validate.isTrue(frameEnd >= frameStart && frameStart >= 0);
    }

    public int total() {
        return frameEnd - frameStart + 1;
    }

}
