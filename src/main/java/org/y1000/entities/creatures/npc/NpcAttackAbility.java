package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Damage;

import java.util.Optional;

public final class NpcAttackAbility implements NpcAbility {

    private final int attackSpeedMillis;

    private final int recoveryMillis;

    private int attackCooldownMillis;

    private int recoveryCooldownMillis;

    private final Damage damage;

    private final int accuracy;

    private final Optional<String> sound;

    public NpcAttackAbility(int attackSpeedMillis,
                            int recoveryMillis,
                            int bodyDamage,
                            int hit,
                            String sound) {
        this.attackSpeedMillis = attackSpeedMillis + 1500;
        this.recoveryMillis = recoveryMillis + 700;
        this.damage = new Damage(bodyDamage, 0, 0, 0);
        this.accuracy = hit + 75;
        this.sound = Optional.ofNullable(sound);
    }

    public void cooldown(int delta) {
        if (attackCooldownMillis > 0)
            attackCooldownMillis -= delta;
        if (recoveryCooldownMillis >= 0)
            recoveryCooldownMillis -= delta;
    }

    public boolean ableToAttack() {
        return attackCooldownMillis <= 0 && recoveryCooldownMillis <= 0;
    }

    public void cooldownRecovery() {
        recoveryCooldownMillis = recoveryMillis;
    }

    public void cooldownAttack() {
        attackCooldownMillis = attackSpeedMillis;
    }

    public void clearCooldown() {
        recoveryCooldownMillis = 0;
        attackCooldownMillis = 0;
    }

    public Damage damage() {
        return damage;
    }

    public Optional<String> sound() {
        return sound;
    }

    public int accuracy() {
        return accuracy;
    }

    @Override
    public boolean update(int delta) {
        return false;
    }

    @Override
    public int apply(Npc npc) {
        return 0;
    }
}
