package org.y1000.entities.creatures;

import org.y1000.entities.attribute.Damage;

/**
 * A creature that attacks.
 */
public interface ViolentCreature extends Creature {
    int attackSpeed();

    int recovery();

    int hit();

    Damage damage();

    int attackCooldown();

    int recoveryCooldown();

    default int cooldown() {
        return Math.max(attackCooldown(), recoveryCooldown());
    }
}
