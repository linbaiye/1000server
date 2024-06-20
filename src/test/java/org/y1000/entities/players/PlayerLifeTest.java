package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerLifeTest {

    private PlayerLife playerLife;

    @BeforeEach
    void setUp() {
        playerLife = new PlayerLife(2000, 100);
    }

    @Test
    void values() {
        assertEquals(2200, playerLife.maxValue());
        assertEquals(2200, playerLife.currentValue());
    }

    @Test
    void gain() {
        var old = playerLife.currentValue();
        playerLife.gain(100);
        assertEquals(old, playerLife.currentValue());
        playerLife.consume(1);
        assertEquals(2199, playerLife.currentValue());
        playerLife.gain(100);
        assertEquals(2200, playerLife.currentValue());
    }

    @Test
    void whenAgeIncreased() {
        playerLife.onAgeIncreased(200);
        assertEquals(2300, playerLife.maxValue());
    }
}