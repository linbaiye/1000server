package org.y1000.entities.objects;

import lombok.Builder;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.EntityLifebarEvent;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;

public final class KillableDynamicObject extends AbstractMutableDynamicObject implements RespawnDynamicObject {

    private final int armor;

    private int life;

    private final int maxLife;

    @Builder
    public KillableDynamicObject(long id,
                                 Coordinate coordinate,
                                 RealmMap realmMap,
                                 DynamicObjectSdb dynamicObjectSdb, String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName);
        this.armor = dynamicObjectSdb.getArmor(idName);
        this.maxLife = dynamicObjectSdb.getLife(idName);
        Validate.isTrue(maxLife > 0);
        this.life = maxLife;
    }

    @Override
    protected void onAnimationDone() {
        realmMap().free(this);
        emitEvent(new RemoveEntityEvent(this));
    }

    private boolean handleDamaged(Damage damage) {
        if (!canBeAttackedNow()) {
            return false;
        }
        var dmg = Math.max(damage.bodyDamage() - armor, 1);
        life -= Math.min(dmg, life);
        if (life == 0) {
            dynamicObjectSdb().getSoundEvent(idName()).ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
            changeAnimation(1, dynamicObjectSdb().getOpenedMillis(idName()));
        } else {
            dynamicObjectSdb().getSoundSpecial(idName()).ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        }
        emitEvent(new EntityLifebarEvent(this, life, maxLife));
        return true;
    }

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

    }

    @Override
    public void attackedBy(Projectile projectile) {

    }

    public void attackedByAoe(Damage damage) {
        handleDamaged(damage);
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

    @Override
    public DynamicObjectType type() {
        return DynamicObjectType.KILLABLE;
    }

    @Override
    public boolean canBeAttackedNow() {
        return getAnimationIndex() == 0 && life > 0;
    }

    @Override
    public void respawn() {
        life = maxLife;
        realmMap().occupy(this);
        changeAnimation(0);
    }

    @Override
    public int respawnTime() {
        return dynamicObjectSdb().getRegenInterval(idName()) * 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TriggerDynamicObject object)) return false;
        return Objects.equals(id(), object.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }
}
