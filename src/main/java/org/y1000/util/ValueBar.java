package org.y1000.util;

import org.apache.commons.lang3.Validate;

public record ValueBar(int max, int current) {
    public ValueBar {
        Validate.isTrue(max > 0);
        Validate.isTrue(current <= max);
    }
}
