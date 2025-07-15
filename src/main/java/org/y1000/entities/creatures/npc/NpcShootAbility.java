package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.event.NpcShootEvent;
import org.y1000.entities.creatures.npc.event.NpcSoundEvent;
import org.y1000.entities.players.Damage;
import org.y1000.entities.projectile.NpcProjectile;
import org.y1000.util.Coordinate;

import java.util.Optional;

public class NpcShootAbility extends AbstractNpcAttackAbility implements EscapeAbility {

    private final String projectileSprite;

    static final int COOLDOWN = 3000;

    private int chargingCooldown;

    private int number;

    private int leftMillisToFire;

    private NpcShootEvent eventToSend;

    public NpcShootAbility(String projectileSprite,
                           String swingSound,
                           int attackSpeed,
                           NpcAnimation npcAnimation,
                           int damage,
                           int accuracy) {
        super(npcAnimation, new Damage(damage, 0, 0, 0), accuracy, swingSound, attackSpeed);
        this.projectileSprite = projectileSprite;
        chargingCooldown = 0;
        resetProjectile();
    }

    private void resetProjectile() {
        number = 5;
    }


    @Override
    public boolean canAttack() {
        return number > 0 && isCooldownOff();
    }

    public void cooldown(int delta) {
        if (number > 0)
            return;
        chargingCooldown -= delta;
        if (chargingCooldown == 0) {
            resetProjectile();
        }
    }

    public boolean shouldEscape(Npc npc, ActiveEntity enemy) {
        return npc.coordinate().directDistance(enemy.coordinate()) < 3 && canAttack();
    }

    public void shoot(Npc npc, ActiveEntity target) {
        if (!canAttack())
            return;
        if (number-- <= 0)
            chargingCooldown = COOLDOWN;
        sendActionAndStartShortAnimation(npc, getAttackSpeedMillis());
        if (getSound() != null)
            npc.sendEvent(NpcSoundEvent.of(npc, getSound()));
        resetAttackCooldown();
        leftMillisToFire = 200;
        NpcProjectile projectile = new NpcProjectile(npc, target, projectileSprite, getAccuracy(), getDamage());
        eventToSend = NpcShootEvent.of(npc, projectile);
    }

    @Override
    public boolean update(int delta) {
        if (eventToSend != null) {
            leftMillisToFire -= delta;
            if (leftMillisToFire <= 0) {
                eventToSend.source().sendEvent(eventToSend);
                eventToSend = null;
            }
        }
        return updateAnimation(delta);
    }

    @Override
    public Optional<Coordinate> computeSafeSpot(Npc npc, ActiveEntity enemy) {
        return Optional.ofNullable(EscapeAbility.doCompute(npc, enemy, 5));
    }
}
