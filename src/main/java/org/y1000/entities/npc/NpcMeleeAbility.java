package org.y1000.entities.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.npc.event.NpcSoundEvent;
import org.y1000.entities.players.Damage;

@Slf4j
public final class NpcMeleeAbility extends AbstractNpcAttackAbility {

    public NpcMeleeAbility(int bodyDamage,
                           int hit,
                           String sound,
                           NpcAnimation animationTimer,
                           int attackSpeedMillis) {
        super(animationTimer, new Damage(bodyDamage, 0, 0, 0), hit,
                sound, attackSpeedMillis);
    }

    public void apply(Npc npc, ActiveEntity target) {
        target.findAbility(HurtAbility.class).ifPresent(hurtAbility ->  {
            hurtAbility.attacked(npc, getDamage(), getAccuracy());
            if (getSound() != null)
                npc.sendEvent(NpcSoundEvent.of(npc, getSound()));
            sendActionAndStartShortAnimation(npc, getAttackSpeedMillis());
            resetAttackCooldown();
        });
    }

    public int attackCooldownLeft() {
        return getCooldownLeft();
    }

    @Override
    public void cooldown(int delta) {
        cooldownAttack(delta);
    }

    @Override
    public boolean canAttack() {
        return isCooldownOff();
    }

    @Override
    public boolean update(int delta) {
        return updateAnimation(delta);
    }
}
