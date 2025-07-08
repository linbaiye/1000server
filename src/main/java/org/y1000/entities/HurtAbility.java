package org.y1000.entities;

import org.y1000.entities.players.Damage;

public interface HurtAbility {

    boolean canBeAttacked();

    int attacked(ActiveEntity attacker, Damage damage, int accuracy);

}
