package org.y1000.entities.objects;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.EntityLifebarEvent;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

public abstract class AbstractKillableDynamicObject extends AbstractMutableDynamicObject {
    private final int armor;
    private final int maxLife;
    private int life;

    public AbstractKillableDynamicObject(long id, Coordinate coordinate, RealmMap realmMap, DynamicObjectSdb dynamicObjectSdb, String idName, Animation[] animations) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName, animations);
        this.armor = dynamicObjectSdb.getArmor(idName);
        this.maxLife = dynamicObjectSdb.getLife(idName);
        this.life = maxLife;
        Validate.isTrue(maxLife > 0);
    }


    boolean doHandleDamaged(Damage damage) {
        if (!canBeAttackedNow()) {
            return false;
        }
        var dmg = Math.max(damage.bodyDamage() - armor, 1);
        life -= Math.min(dmg, life);
        if (life == 0) {
            changeAnimation(1, dynamicObjectSdb().getOpenedMillis(idName()));
            emitEvent(new DynamicObjectDieEvent(this));
            dynamicObjectSdb().getSoundEvent(idName()).ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        } else {
            dynamicObjectSdb().getSoundSpecial(idName()).ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        }
        emitEvent(new EntityLifebarEvent(this, life, maxLife));
        return true;
    }

    abstract boolean handleDamaged(Damage damage);

    @Override
    public boolean attackedBy(Player attacker) {
        Validate.notNull(attacker);
        if (occupyingCoordinates().stream().noneMatch(coordinate -> coordinate.directDistance(attacker.coordinate()) <= 1)) {
            return false;
        }
        return handleDamaged(attacker.damage());
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {
        Validate.notNull(attacker);
        if (occupyingCoordinates().stream()
                .anyMatch(coordinate -> coordinate.directDistance(attacker.coordinate()) <= 1)) {
            handleDamaged(attacker.damage());
        }
    }

    @Override
    public void attackedBy(Projectile projectile) {
        handleDamaged(projectile.damage());
    }

    public void attackedByAoe(Damage damage) {
        handleDamaged(damage);
    }

    void resetLife() {
        life = maxLife;
    }

    int currentLife() {
        return life;
    }

    @Override
    public void update(int delta) {
        if (getAnimationIndex() != 0)
            updateAnimation(delta);
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new DynamicObjectInterpolation(this, animationElapsedDuration());
    }
}
