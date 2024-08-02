package org.y1000.entities.objects;

import lombok.Builder;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.players.Damage;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;

public final class KillableDynamicObject extends AbstractKillableDynamicObject implements RespawnDynamicObject {

    @Builder
    public KillableDynamicObject(long id,
                                 Coordinate coordinate,
                                 RealmMap realmMap,
                                 DynamicObjectSdb dynamicObjectSdb, String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName, parseAnimationFrames(idName, dynamicObjectSdb));
    }

    @Override
    public DynamicObjectType type() {
        return DynamicObjectType.KILLABLE;
    }

    @Override
    public void respawn() {
        resetLife();
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

    @Override
    public boolean canBeAttackedNow() {
        return getAnimationIndex() == 0 && currentLife() > 0;
    }

    @Override
    protected void onAnimationDone() {
        realmMap().free(this);
        emitEvent(new RemoveEntityEvent(this));
    }

    @Override
    boolean handleDamaged(Damage damage) {
        return doHandleDamaged(damage);
    }
}
