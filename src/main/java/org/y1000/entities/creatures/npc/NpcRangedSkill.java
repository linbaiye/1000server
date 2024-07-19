package org.y1000.entities.creatures.npc;

import lombok.Getter;

public class NpcRangedSkill {
    @Getter
    private final int projectileSpriteId;

    @Getter
    private final String swingSound;

    private static final int COOLDOWN = 3000;

    private int cooldown;

    private int counter;

    public NpcRangedSkill(int projectileSpriteId,
                          String swingSound) {
        this.projectileSpriteId = projectileSpriteId;
        this.swingSound = swingSound;
        cooldown = COOLDOWN;
        resetCounter();
    }

    private void resetCounter() {
        counter = 5;
    }

    public boolean isAvailable() {
        return counter > 0;
    }

    public void cooldown(int delta) {
        if (counter > 0) {
            return;
        }
        cooldown -= delta;
        if (cooldown == 0) {
            resetCounter();
        }
    }

    public void consumeCounter() {
        if (--counter <= 0) {
            cooldown = COOLDOWN;
        }
    }
}
