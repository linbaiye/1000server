package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Damage;

public abstract class AbstractNpcAttackAbility extends AbstractNpcNonMoveAbility implements CooldownAbility {

    private final Damage damage;

    private final int accuracy;

    private final String sound;

    private final int attackSpeedMillis;

    private int cooldownLeft;


    int getAttackSpeedMillis() {
        return attackSpeedMillis;
    }

    public AbstractNpcAttackAbility(NpcAnimation animation,
                                    Damage damage,
                                    int accuracy,
                                    String sound,
                                    int attackSpeedMillis) {
        super(animation);
        this.damage = damage;
        this.accuracy = accuracy;
        this.sound = sound;
        this.attackSpeedMillis = attackSpeedMillis;
        cooldownLeft = 0;
    }

    int getAccuracy() {
        return accuracy;
    }

    String getSound() {
        return sound;
    }

    void cooldownAttack(int delta) {
        cooldownLeft = cooldownLeft > delta ? cooldownLeft - delta : 0;
    }

    int getCooldownLeft() {
        return cooldownLeft;
    }

    void resetAttackCooldown() {
        cooldownLeft = attackSpeedMillis;
    }

    Damage getDamage() {
        return damage;
    }

    public abstract boolean canAttack();

    boolean isCooldownOff() {
        return cooldownLeft <= 0;
    }

}
