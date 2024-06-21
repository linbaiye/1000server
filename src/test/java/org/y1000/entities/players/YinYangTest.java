package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class YinYangTest {
    private YinYang yinYang;
    private YinYang.YinYangDecider mockedDecider;

    @BeforeEach
    void setUp() {
        mockedDecider = Mockito.mock(YinYang.YinYangDecider.class);
        yinYang = new YinYang(0, 0, mockedDecider);
    }

    @Test
    void age() {
        assertEquals(100, yinYang.age());
    }

    @Test
    void levels() {
        assertEquals(500, yinYang.yinLevel());
        assertEquals(500, yinYang.yangLevel());
    }

    @Test
    void accumulate() {
        when(mockedDecider.isYin()).thenReturn(true);
        YinYang accumulated = yinYang.accumulate(9);
        assertNotEquals(accumulated.yinLevel(), yinYang.yinLevel());
        assertEquals(accumulated.yangLevel(), yinYang.yangLevel());
        assertNotEquals(accumulated.age(), yinYang.age());
        Mockito.reset(mockedDecider);
        when(mockedDecider.isYin()).thenReturn(false);
        accumulated = yinYang.accumulate(9);
        assertEquals(accumulated.yinLevel(), yinYang.yinLevel());
        assertNotEquals(accumulated.yangLevel(), yinYang.yangLevel());
        assertNotEquals(accumulated.age(), yinYang.age());
    }

    @Test
    void hasHigherLevel() {
        when(mockedDecider.isYin()).thenReturn(true);
        var newYiyang = yinYang.accumulate(9);
        assertTrue(newYiyang.hasHigherLevel(yinYang));
    }
}