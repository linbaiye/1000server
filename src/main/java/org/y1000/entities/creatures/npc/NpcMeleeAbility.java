package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.creatures.npc.event.NpcSoundEvent;
import org.y1000.entities.players.Damage;

@Slf4j
public final class NpcMeleeAbility extends AbstractNpcNonMoveAbility implements Cooldown {

    private final NpcAttackSpeed npcAttackSpeed;

    private final Damage damage;

    private final int accuracy;

    private final String sound;

    public NpcMeleeAbility(int bodyDamage,
                           int hit,
                           String sound,
                           NpcAnimation animationTimer,
                           NpcAttackSpeed npcAttackSpeed) {
        super(animationTimer);
        this.npcAttackSpeed = npcAttackSpeed;
        this.damage = new Damage(bodyDamage, 0, 0, 0);
        this.accuracy = hit;
        this.sound = StringUtils.isEmpty(sound) ? null : sound;
    }

    public boolean cooldown(int delta) {
        return npcAttackSpeed.cooldown(delta);
    }

    @Override
    public void startCooldown() {
        npcAttackSpeed.startCooldown();
    }

    public boolean isCooldownOff() {
        return npcAttackSpeed.isCooldownOff();
    }

    public int cooldownLeft() {
        return npcAttackSpeed.cooldownLeft();
    }

    public void apply(Npc npc, ActiveEntity target) {
        target.findAbility(HurtAbility.class).ifPresent(hurtAbility ->  {
            hurtAbility.attacked(npc, damage, accuracy);
            if (sound != null)
                npc.sendEvent(NpcSoundEvent.of(npc, sound));
            sendActionAndStartShortAnimation(npc, npcAttackSpeed.getAttackSpeedMillis());
            startCooldown();
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
