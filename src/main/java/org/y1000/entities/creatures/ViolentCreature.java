package org.y1000.entities.creatures;

import org.y1000.entities.attribute.Damage;
import org.y1000.realm.Realm;

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

    void cooldownRecovery();

    void cooldownAttack();

    default int cooldown() {
        return Math.max(attackCooldown(), recoveryCooldown());
    }
}
