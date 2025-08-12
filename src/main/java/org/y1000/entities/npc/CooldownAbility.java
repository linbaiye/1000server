package org.y1000.entities.npc;

public interface CooldownAbility {
    /**
     * Cooldown the ability.
     * @param delta
     */
    void cooldown(int delta);
}
