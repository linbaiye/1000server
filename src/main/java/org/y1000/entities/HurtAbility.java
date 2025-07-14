package org.y1000.entities;

import org.y1000.entities.players.Damage;

public interface HurtAbility {

    boolean canBeAttacked();

    /**
     * Allow a player to swing a strike?
     * @return true if allowed.
     */
    boolean swingAllowed();

    /**
     *
     * @param attacker
     * @param damage
     * @param accuracy
     * @return -1 if it's a miss or not attackable, the exp otherwise.
     */
    int attacked(ActiveEntity attacker, Damage damage, int accuracy);

    int currentLife();

}
