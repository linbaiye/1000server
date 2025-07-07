package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.Damage;

import java.util.function.Consumer;

public final class HurtAbility {

    private final int armor;

    private final int avoidance;

    private final int hurtSound;

    @Getter
    private int currentLife;

    @Getter
    private final int maxLife;

    private HurtTrigger hurtTrigger;

    public interface HurtTrigger {
        void onHurt(ActiveEntity activeEntity, Damage damage, int hit);
    }


    public HurtAbility(int armor,
                       int avoidance,
                       int hurtSound,
                       int currentLife,
                       int maxLife) {
        this.armor = armor;
        this.avoidance = avoidance;
        this.hurtSound = hurtSound;
        this.currentLife = currentLife;
        this.maxLife = maxLife;
    }

    public void setHurtTrigger(HurtTrigger trigger) {
        this.hurtTrigger = trigger;
    }

    public void attacked(ActiveEntity activeEntity, Damage damage, int hit) {
        if (this.hurtTrigger != null) {
            hurtTrigger.onHurt(activeEntity, damage, hit);
        }
    }
}
