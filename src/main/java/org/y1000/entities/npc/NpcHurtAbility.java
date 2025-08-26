package org.y1000.entities.npc;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.npc.event.NpcLifeBarEvent;
import org.y1000.entities.npc.event.NpcSoundEvent;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.ExperienceUtil;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;


public final class NpcHurtAbility extends AbstractNpcNonMoveAbility implements HurtAbility, CooldownAbility {

    private final int armor;

    private final int avoidance;

    private final String hurtSound;

    private int currentLife;

    @Getter
    private final int maxLife;

    @Setter
    private BiConsumer<? super ActiveEntity, NpcHurtAbility> hurtTrigger;

    @Getter
    private NpcAnimatedAbility interruptedAbility;

    private final int recoveryMillis;

    private int recoveryLeft;


    public NpcHurtAbility(int armor,
                          int avoidance,
                          String hurtSound,
                          int maxLife,
                          NpcAnimation animationTimer,
                          int recoveryMillis) {
        super(animationTimer);
        Validate.isTrue(maxLife > 0);
        this.armor = armor;
        this.avoidance = avoidance;
        this.hurtSound = StringUtils.isEmpty(hurtSound) ? null : hurtSound;
        this.maxLife = maxLife;
        this.currentLife = maxLife;
        this.recoveryMillis = recoveryMillis;
        recoveryLeft = 0;
    }

    private boolean isDodged(int accuracy) {
        var rand = ThreadLocalRandom.current().nextInt(0, accuracy + avoidance);
        return rand < avoidance;
    }

    public void cooldown(int delta) {
        if (recoveryLeft > 0)
            recoveryLeft -= delta;
    }

    public int recoveryLeft() {
        return recoveryLeft;
    }

    public boolean isRecovered() {
        return recoveryLeft <= 0;
    }

    public boolean canBeAttacked() {
        return currentLife > 0;
    }

    @Override
    public boolean swingAllowed() {
        return canBeAttacked();
    }

    public void instantKill() {
        currentLife = 0;
    }

    public void apply(Npc npc, NpcAnimatedAbility interruptedAbility) {
        sendActionAndStartShortAnimation(npc, recoveryMillis);
        npc.sendEvent(NpcLifeBarEvent.of(npc, currentLife, maxLife));
        if (hurtSound != null) {
            npc.sendEvent(NpcSoundEvent.of(npc, hurtSound));
        }
        if (interruptedAbility instanceof NpcHurtAbility hurtAbility) {
            this.interruptedAbility = hurtAbility.interruptedAbility;
        } else {
            this.interruptedAbility = interruptedAbility;
        }
    }

    public int attacked(ActiveEntity activeEntity, Damage damage, int accuracy) {
        if (!canBeAttacked() || isDodged(accuracy))
            return -1;
        var before = currentLife;
        int bodyDamage = damage.bodyDamage() - armor;
        bodyDamage = bodyDamage > 0 ? bodyDamage : 1;
        currentLife = currentLife > bodyDamage ? currentLife - bodyDamage : 0;
        int damageTaken = before - currentLife;
        if (this.hurtTrigger != null) {
            hurtTrigger.accept(activeEntity, this);
        }
        recoveryLeft = recoveryMillis;
        return ExperienceUtil.damageToExp(maxLife, damageTaken);
    }

    @Override
    public int currentLife() {
        return currentLife;
    }


    @Override
    public int maxLife() {
        return maxLife;
    }

    public boolean isDead() {
        return currentLife <= 0;
    }


    @Override
    public boolean update(int delta) {
        return updateAnimation(delta);
    }

}
