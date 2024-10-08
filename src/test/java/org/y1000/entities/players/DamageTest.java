package org.y1000.entities.players;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DamageTest {

    @Test
    void addNoNegative() {
        Damage damage = new Damage(1, 1, 1, 1);
        Damage adding = new Damage(-2, 1, -2, -2);
        Damage added = damage.addNoNegative(adding);
        assertEquals(new Damage(0, 2, 0, 0), added);
    }
}