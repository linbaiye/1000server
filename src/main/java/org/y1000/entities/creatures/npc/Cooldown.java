package org.y1000.entities.creatures.npc;

public interface Cooldown {

    /**
     * How long will this ability to be off cooldown in millis.
     * @return
     */
    int cooldownLeft();

    /**
     * Is the ability cooldown off?
     * @return
     */
    boolean isCooldownOff();

    /**
     * Cooldown the ability, return true if it is off.
     * @param delta
     * @return true if off.
     */
    boolean cooldown(int delta);

    void startCooldown();
}
