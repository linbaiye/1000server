package org.y1000.entities.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.npc.event.NpcShootEvent;
import org.y1000.entities.npc.event.NpcSoundEvent;
import org.y1000.entities.players.Damage;
import org.y1000.entities.projectile.NpcProjectile;
import org.y1000.util.Coordinate;

import java.util.Optional;

@Slf4j
public class NpcShootAbility extends AbstractNpcAttackAbility {

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
        this.projectileSprite = "y" + projectileSprite;
        chargingCooldown = COOLDOWN;
        resetProjectile();
    }

    private void resetProjectile() {
        number = 5;
    }


    public boolean hasProjectile() {
        return number > 0;
    }

    @Override
    public boolean canAttack() {
        return hasProjectile() && isCooldownOff();
    }

    public void cooldown(int delta) {
        cooldownAttack(delta);
        if (number > 0)
            return;
        chargingCooldown -= delta;
        if (chargingCooldown <= 0) {
            resetProjectile();
            chargingCooldown = COOLDOWN;
        }
    }

    public int cooldownLeft() {
        return getCooldownLeft();
    }

    public boolean shouldEscape(Npc npc, ActiveEntity enemy) {
        return npc.coordinate().directDistance(enemy.coordinate()) < 3 && canAttack();
    }

    public Optional<Direction> computeDirectionToSafeSpot(Npc npc, ActiveEntity entity) {
        if (npc.coordinate().directDistance(entity.coordinate()) >= 3) {
            return Optional.empty();
        }
        Direction direction = entity.coordinate().directionTo(npc.coordinate());
        Coordinate coordinate = npc.coordinate().moveBy(direction);
        if (npc.realmMap().movable(coordinate))
            return Optional.of(direction);
        for (Direction neighbour : direction.neighbours()) {
            coordinate = npc.coordinate().moveBy(neighbour);
            if (npc.realmMap().movable(coordinate))
                return Optional.of(neighbour);
        }
        return Optional.empty();
    }


    public void shoot(Npc npc, ActiveEntity target) {
        if (!canAttack())
            return;
        if (number-- <= 0)
            chargingCooldown = COOLDOWN;
        npc.changeDirection(npc.coordinate().directionByAngle(target.coordinate()));
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
}
