package org.y1000.entities.creatures.npc;

import lombok.Getter;

public final class NpcRangedSkill {
    @Getter
    private final int projectileSpriteId;

    @Getter
    private final String swingSound;

    public static final int COOLDOWN = 3000;

    private int cooldown;

    private int projectile;

    public NpcRangedSkill(int projectileSpriteId,
                          String swingSound) {
        this.projectileSpriteId = projectileSpriteId;
        this.swingSound = swingSound;
        cooldown = COOLDOWN;
        resetCounter();
    }

    private void resetCounter() {
        projectile = 5;
    }

    public boolean isAvailable() {
        return projectile > 0;
    }

    public void cooldown(int delta) {
        if (projectile > 0) {
            return;
        }
        cooldown -= delta;
        if (cooldown == 0) {
            resetCounter();
        }
    }
    
    public boolean isInRange(int dist) {
        return dist >= 3 && dist <= 5;
    }
    
    public boolean isLtLower(int dis) {
        return dis < 3;
    }

    public void consumeProjectile() {
        if (--projectile <= 0) {
            cooldown = COOLDOWN;
        }
    }
}
