package org.y1000.entities;

import org.y1000.entities.players.Damage;

public interface HurtAbility {

    boolean canBeAttacked();

    boolean canBeSwung();

    /**
     *
     * @param attacker
     * @param damage
     * @param accuracy
     * @return -1 if it's a miss, the exp otherwise.
     */
    int attacked(ActiveEntity attacker, Damage damage, int accuracy);

}
