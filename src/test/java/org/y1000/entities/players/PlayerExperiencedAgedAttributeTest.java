package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerExperiencedAgedAttributeTest {

    private PlayerExperiencedAgedAttribute attribute;

    @BeforeEach
    void setUp() {
        attribute = new PlayerExperiencedAgedAttribute( 0, 100);
    }

    @Test
    void maxValue() {
        assertEquals(150, attribute.maxValue());
    }

    @Test
    void hasEnough() {
        assertTrue(attribute.hasEnough(150));
        assertFalse(attribute.hasEnough(151));
    }

    @Test
    void gain() {
        attribute.gain(100);
        assertEquals(attribute.maxValue(), attribute.currentValue());
        int current = attribute.currentValue();
        attribute.consume(10);
        attribute.gain(1);
        assertEquals(current - 10 + 1, attribute.currentValue());
        attribute.gain(99);
        assertEquals(attribute.maxValue(), attribute.currentValue());
    }

    @Test
    void consume() {
        attribute.consume(1);
        assertEquals(attribute.maxValue() - 1, attribute.currentValue());
        attribute.consume(attribute.maxValue());
        assertEquals(0, attribute.currentValue());
        attribute.consume(1);
        assertEquals(0, attribute.currentValue());
    }

    @Test
    void gainExpIfLowEnough() {
        int oldMax = attribute.maxValue();
        attribute.consume((int)(oldMax * 0.89));
        attribute.gain((int)(oldMax * 0.99));
        assertEquals(oldMax, attribute.maxValue());

        attribute.consume((int)(oldMax * 0.95));
        attribute.gain((int)(oldMax * 0.99));
        assertNotEquals(oldMax, attribute.maxValue());
    }

    @Test
    void onAgeIncreased() {
        var oldMax = attribute.maxValue();
        assertFalse(attribute.onAgeIncreased(101));
        assertEquals(oldMax, attribute.maxValue());
        assertTrue(attribute.onAgeIncreased(200));
        assertNotEquals(oldMax, attribute.maxValue());
    }
}