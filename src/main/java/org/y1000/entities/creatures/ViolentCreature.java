package org.y1000.entities.creatures;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Damage;

/**
 * A creature that attacks.
 */
public interface ViolentCreature extends Creature {
    int attackSpeed();

    int recovery();

    int hit();


    Damage damage();

    /**
     * How long will attack cooldown take in milliseconds
     * @return
     */
    int attackCooldown();

    /**
     * How long will recovery cooldown take in milliseconds
     * @return
     */
    int recoveryCooldown();

    /**
     * Start Cooldown recovery.
     */
    void cooldownRecovery();

    /**
     * Start Cooldown attack.
     */
    void cooldownAttack();


    default int maxCooldown() {
        return Math.max(attackCooldown(), recoveryCooldown());
    }
}
