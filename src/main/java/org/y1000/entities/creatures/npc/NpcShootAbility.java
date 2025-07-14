package org.y1000.entities.creatures.npc;

import org.y1000.message.NpcSnapshot;

public class NpcShootAbility implements NpcAbility, Cooldown {

    private final int spriteId;

    private final String swingSound;

    static final int COOLDOWN = 3000;

    private int cooldown;

    private int projectile;

    public NpcShootAbility(int spriteId,
                           String swingSound,
                           NpcAttackSpeed attackSpeed) {
        this.spriteId = spriteId;
        this.swingSound = swingSound;
        cooldown = 0;
        resetProjectile();
    }

    private void resetProjectile() {
        projectile = 5;
    }

    @Override
    public int cooldownLeft() {
        return projectile > 0 ? 0 : cooldown;
    }

    @Override
    public boolean isCooldownOff() {
        return projectile > 0 || cooldown <= 0;
    }

    @Override
    public boolean cooldown(int delta) {
        if (isCooldownOff())
            return true;
        cooldown -= delta;
        return isCooldownOff();
    }

    @Override
    public void startCooldown() {

    }

    @Override
    public boolean update(int delta) {
        return false;
    }

    @Override
    public NpcSnapshot captureSnapshot(Npc npc) {
        return null;
    }
}
