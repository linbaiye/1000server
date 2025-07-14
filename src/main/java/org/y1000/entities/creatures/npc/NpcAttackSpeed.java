package org.y1000.entities.creatures.npc;

import lombok.Getter;

public final class NpcAttackSpeed implements Cooldown {
    @Getter
    private final int attackSpeedMillis;

    private int attackCooldownMillis;

    public NpcAttackSpeed(int attackSpeedMillis) {
        this.attackSpeedMillis = attackSpeedMillis;
        this.attackCooldownMillis = 0;
    }

    @Override
    public int cooldownLeft() {
        return attackCooldownMillis;
    }

    @Override
    public boolean isCooldownOff() {
        return attackCooldownMillis == 0;
    }

    @Override
    public boolean cooldown(int delta) {
        if (isCooldownOff())
            return true;
        attackCooldownMillis -= delta;
        return isCooldownOff();
    }

    @Override
    public void startCooldown() {
        attackCooldownMillis = attackSpeedMillis;
    }
}
