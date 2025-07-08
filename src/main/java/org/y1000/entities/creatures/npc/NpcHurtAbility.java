package org.y1000.entities.creatures.npc;

import lombok.Getter;
import lombok.Setter;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.creatures.npc.event.NpcLifeBarEvent;
import org.y1000.entities.players.Damage;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;


public final class NpcHurtAbility extends AbstractNonMoveAbility implements HurtAbility {

    private final int armor;

    private final int avoidance;

    private final Optional<String> hurtSound;

    @Getter
    private int currentLife;

    @Getter
    private final int maxLife;


    @Setter
    private BiConsumer<? super ActiveEntity, NpcHurtAbility> hurtTrigger;

    @Getter
    private NpcAbility interruptedAbility;


    public NpcHurtAbility(int armor,
                          int avoidance,
                          String hurtSound,
                          int maxLife,
                          NpcAnimation animationTimer) {
        super(animationTimer);
        this.armor = armor;
        this.avoidance = avoidance + 20;
        this.hurtSound = Optional.ofNullable(hurtSound);
        this.maxLife = maxLife;
        this.currentLife = maxLife;
    }

    private boolean isDodged(int accuracy) {
        var rand = ThreadLocalRandom.current().nextInt(0, accuracy + avoidance);
        return rand < avoidance;
    }

    public boolean canBeAttacked() {
        return currentLife > 0;
    }

    public void apply(Npc npc, NpcAbility interruptedAbility) {
        sendActionAndStartAnimation(npc);
        npc.sendEvent(NpcLifeBarEvent.of(npc, currentLife, maxLife));
        if (interruptedAbility instanceof NpcHurtAbility hurtAbility) {
            this.interruptedAbility = hurtAbility.interruptedAbility;
        } else {
            this.interruptedAbility = interruptedAbility;
        }
    }

    public int attacked(ActiveEntity activeEntity, Damage damage, int accuracy) {
        if (!canBeAttacked())
            return -1;
        if (isDodged(accuracy))
            return  - 1;
        var before = currentLife;
        int bodyDamage = damage.bodyDamage() - armor;
        bodyDamage = bodyDamage > 0 ? bodyDamage : 1;
        currentLife = currentLife > bodyDamage ? currentLife - bodyDamage : 0;
        int damageTaken = Math.max(before - currentLife, 1);
        if (this.hurtTrigger != null) {
            hurtTrigger.accept(activeEntity, this);
        }
        return damageTaken;
    }

    public Optional<String> hurtSound() {
        return hurtSound;
    }
}
