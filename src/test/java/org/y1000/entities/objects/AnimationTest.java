package org.y1000.entities.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimationTest {

    @Test
    void total() {
        assertEquals(1, new IAnimation(0, 0, false).total());
        assertEquals(2, new IAnimation(1, 2, false).total());
    }
}