package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.monster.NpcAnimationEnum;

public class AttackAction implements NpcAction {
    private final int attackSpeed;
    private final int recovery;

    private int attackCooldown;
    private int recoveryCooldown;

    public AttackAction(int attackSpeed, int recovery) {
        this.attackSpeed = attackSpeed;
        this.recovery = recovery;
    }

    public void ResetRecoveryCooldown() {
        recoveryCooldown = recovery;
    }

    public void cooldown(int delta) {
        if (attackCooldown > 0)
            attackCooldown -= delta;
        if (recoveryCooldown > 0)
            recoveryCooldown -= delta;
    }


    @Override
    public boolean update(int delta) {
        return false;
    }

    @Override
    public int elapsedMillis() {
        return 0;
    }

    @Override
    public NpcAnimationEnum actionEnum() {
        return null;
    }

}
