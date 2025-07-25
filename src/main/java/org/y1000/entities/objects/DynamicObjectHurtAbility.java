package org.y1000.entities.objects;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Damage;

public class DynamicObjectHurtAbility implements HurtAbility {

    private final int maxLife;

    private int currentLife;

    public DynamicObjectHurtAbility(int maxLife) {
        this.maxLife = maxLife;
        currentLife = maxLife;
    }

    @Override
    public boolean canBeAttacked() {
        return currentLife() > 0;
    }

    @Override
    public boolean swingAllowed() {
        return false;
    }

    @Override
    public int attacked(ActiveEntity attacker, Damage damage, int accuracy) {
        currentLife -= damage.bodyDamage();
        if (currentLife < 0)
            currentLife = 0;
        return -1;
    }

    @Override
    public int currentLife() {
        return currentLife;
    }
}
