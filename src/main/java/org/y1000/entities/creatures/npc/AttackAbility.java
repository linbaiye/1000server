package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableEntity;

public class AttackAbility {
    private final int attackSpeed;
    private final int recovery;

    private AttackableEntity enemy;

    public void setEnemy(AttackableEntity enemy) {
        this.enemy = enemy;
    }

    public AttackAbility(int attackSpeed, int recovery) {
        this.attackSpeed = attackSpeed;
        this.recovery = recovery;
    }
}
