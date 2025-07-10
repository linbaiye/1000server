package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.creatures.npc.event.NpcSoundEvent;
import org.y1000.entities.players.Damage;

@Slf4j
public final class NpcAttackAbility extends AbstractNonMoveAbility {

    private final int attackSpeedMillis;

    private int attackCooldownMillis;

    private final Damage damage;

    private final int accuracy;

    private final String sound;

    public NpcAttackAbility(int attackSpeedMillis,
                            int bodyDamage,
                            int hit,
                            String sound,
                            NpcAnimation animationTimer) {
        super(animationTimer);
        this.attackSpeedMillis = attackSpeedMillis;
        this.damage = new Damage(bodyDamage, 0, 0, 0);
        this.accuracy = hit;
        this.sound = StringUtils.isEmpty(sound) ? null : sound;
        attackCooldownMillis = 0;
    }

    public boolean cooldown(int delta) {
        if (attackCooldownMillis > 0)
            attackCooldownMillis -= delta;
        return attackReady();
    }

    public boolean attackReady() {
        return attackCooldownMillis <= 0;
    }

    public int cooldownLeft() {
        return attackCooldownMillis;
    }

    public void apply(Npc npc, ActiveEntity target) {
        target.findAbility(HurtAbility.class).ifPresent(hurtAbility ->  {
            hurtAbility.attacked(npc, damage, accuracy);
            if (sound != null)
                npc.sendEvent(NpcSoundEvent.of(npc, sound));
            sendActionAndStartShortAnimation(npc, attackSpeedMillis);
            log.debug("Animation {}, attackSpeed {}.", animationLength(), attackSpeedMillis);
            attackCooldownMillis = attackSpeedMillis;
        });
    }

    public int accuracy() {
        return accuracy;
    }

    @Override
    public boolean update(int delta) {
        return updateAnimation(delta);
    }
}
