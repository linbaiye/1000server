package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.creatures.npc.event.NpcSoundEvent;
import org.y1000.entities.players.Damage;

import java.util.Optional;

public final class NpcAttackAbility extends AbstractNonMoveAbility {

    private final int attackSpeedMillis;

    private final int recoveryMillis;

    private int attackCooldownMillis;

    private int recoveryCooldownMillis;

    private final Damage damage;

    private final int accuracy;

    private final String sound;

    public NpcAttackAbility(int attackSpeedMillis,
                            int recoveryMillis,
                            int bodyDamage,
                            int hit,
                            String sound,
                            NpcAnimation animationTimer) {
        super(animationTimer);
        this.attackSpeedMillis = attackSpeedMillis + 1500;
        this.recoveryMillis = recoveryMillis + 700;
        this.damage = new Damage(bodyDamage, 0, 0, 0);
        this.accuracy = hit + 75;
        this.sound = StringUtils.isEmpty(sound) ? null : sound;
    }

    public void cooldown(int delta) {
        if (attackCooldownMillis > 0)
            attackCooldownMillis -= delta;
        if (recoveryCooldownMillis >= 0)
            recoveryCooldownMillis -= delta;
    }

    public boolean ableToAttack() {
        return attackCooldownMillis <= 0 && recoveryCooldownMillis <= 0;
    }

    public void cooldownRecovery() {
        recoveryCooldownMillis = recoveryMillis;
    }

    private void cooldownAttack() {
        attackCooldownMillis = attackSpeedMillis;
    }

    public int cooldown() {
        return Math.max(attackCooldownMillis, recoveryCooldownMillis);
    }

    public void apply(Npc npc, ActiveEntity target) {
        target.findAbility(HurtAbility.class).ifPresent(hurtAbility ->  {
            hurtAbility.attacked(npc, damage, accuracy);
            if (sound != null)
                npc.sendEvent(NpcSoundEvent.of(npc, sound));
            sendActionAndStartAnimation(npc);
            cooldownAttack();
        });
    }

    public int accuracy() {
        return accuracy;
    }

}
