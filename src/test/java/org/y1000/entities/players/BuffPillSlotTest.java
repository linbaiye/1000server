package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.item.BuffPill;

import static org.junit.jupiter.api.Assertions.*;

class BuffPillSlotTest {

    private BuffPillSlot buffPillSlot;
    @BeforeEach
    void setUp() {
        buffPillSlot = new BuffPillSlot();
    }

    @Test
    void canTake() {
        assertTrue(buffPillSlot.canTake());
        buffPillSlot.take(new BuffPill("test", "test", "test", "test", 2, 1000, 1));
        assertFalse(buffPillSlot.canTake());
    }

    @Test
    void take() {
        Damage damage = Damage.DEFAULT;
        buffPillSlot.take(new BuffPill("test", "test", "test", "test", 2, 1000, 1));
        assertEquals(Damage.DEFAULT.add(new Damage(2, 0,0,0)), buffPillSlot.apply(damage));
    }

    @Test
    void update() {
        buffPillSlot.take(new BuffPill("test", "test", "test", "test", 2, 1000, 1));
        assertTrue(buffPillSlot.isEffective());
        buffPillSlot.update(1000);
        assertFalse(buffPillSlot.isEffective());
    }

    @Test
    void cancel() {
        buffPillSlot.take(new BuffPill("test", "test", "test", "test", 2, 1000, 1));
        assertTrue(buffPillSlot.isEffective());
        buffPillSlot.cancel();
        assertFalse(buffPillSlot.isEffective());
    }

    @Test
    void apply() {
        Damage damage = Damage.DEFAULT;
        buffPillSlot.take(new BuffPill("test", "test", "test", "test", -100, 1000, 1));
        assertEquals(new Damage(0, Damage.DEFAULT.headDamage(),Damage.DEFAULT.armDamage(), Damage.DEFAULT.legDamage()), buffPillSlot.apply(damage));
    }
}