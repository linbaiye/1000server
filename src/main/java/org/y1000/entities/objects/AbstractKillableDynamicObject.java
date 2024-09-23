package org.y1000.entities.objects;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.EntityLifebarEvent;
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
    @Getter
    private final int maxLife;
    private int life;

    public AbstractKillableDynamicObject(long id, Coordinate coordinate, RealmMap realmMap, DynamicObjectSdb dynamicObjectSdb, String idName, Animation[] animations) {
        this(id, coordinate, realmMap, dynamicObjectSdb, dynamicObjectSdb.getLife(idName), idName, animations);
    }

    public AbstractKillableDynamicObject(long id, Coordinate coordinate, RealmMap realmMap, DynamicObjectSdb dynamicObjectSdb, int health, String idName, Animation[] animations) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName, animations);
        this.armor = dynamicObjectSdb.getArmor(idName);
        this.maxLife = dynamicObjectSdb.getLife(idName);
        this.life = health;
        Validate.isTrue(maxLife > 0);
    }

    void damageLife(Damage damage) {
        if (!canBeAttackedNow()) {
            return;
        }
        var dmg = Math.max(damage.bodyDamage() - armor, 1);
        life -= Math.min(dmg, life);
        emitEvent(new EntityLifebarEvent(this, life, maxLife));
    }

    abstract void handleDamaged(Damage damage);

    @Override
    public boolean attackedBy(Player attacker) {
        Validate.notNull(attacker);
        if (!canBeAttackedNow() || !canBeMeleeAt(attacker.coordinate()))
            return false;
        handleDamaged(attacker.damage());
        return true;
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {
        Validate.notNull(attacker);
        if (canBeAttackedNow() && canBeMeleeAt(attacker.coordinate()))
            handleDamaged(attacker.damage());
    }

    @Override
    public void attackedBy(Projectile projectile) {
        if (canBeAttackedNow())
            handleDamaged(projectile.damage());
    }

    public void attackedByAoe(Damage damage) {
        if (canBeAttackedNow())
            handleDamaged(damage);
    }


    public int currentLife() {
        return life;
    }


    @Override
    public void update(int delta) {
        if (getAnimationIndex() != 0)
            updateAnimation(delta);
    }

    @Override
    public boolean canBeMeleeAt(Coordinate coordinate) {
        return coordinate != null &&
                occupyingCoordinates().stream().anyMatch(coor -> coor.directDistance(coordinate) < 2);
    }

    @Override
    public boolean canBeAttackedNow() {
        return getAnimationIndex() == 0 && currentLife() > 0;
    }


    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new DynamicObjectInterpolation(this, animationElapsedDuration());
    }
}
