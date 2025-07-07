package org.y1000.entities.creatures.npc;

import java.util.Optional;

public final class AttackAbility {

    private final int attackSpeedMillis;

    private final int recoveryMillis;

    private int attackCooldownMillis;

    private int recoveryCooldownMillis;

    private final int damage;

    private final Optional<String> sound;

    public AttackAbility(int attackSpeedMillis,
                         int recoveryMillis,
                         int damage,
                         String sound) {
        this.attackSpeedMillis = attackSpeedMillis;
        this.recoveryMillis = recoveryMillis;
        this.damage = damage;
        this.sound = Optional.ofNullable(sound);
    }

    public void cooldown(int delta) {
        if (attackCooldownMillis > 0)
            attackCooldownMillis -= delta;
        if (recoveryCooldownMillis >= 0)
            recoveryCooldownMillis -= delta;
    }

    public boolean isCooldown() {
        return attackCooldownMillis <= 0 && recoveryCooldownMillis <= 0;
    }

    public void cooldownRecovery() {
        recoveryCooldownMillis = recoveryMillis;
    }

    public void cooldownAttackSpeed() {
        attackCooldownMillis = attackSpeedMillis;
    }

    public void clearCooldown() {
        recoveryCooldownMillis = 0;
        attackCooldownMillis = 0;
    }

    public Optional<String> sound() {
        return sound;
    }
}
