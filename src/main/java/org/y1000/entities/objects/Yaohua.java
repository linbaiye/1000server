package org.y1000.entities.objects;

import lombok.Builder;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.players.Damage;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;

public final class Yaohua extends AbstractKillableDynamicObject
        implements EntityEventListener {

    private int fireLighted;

    @Builder
    public Yaohua(long id, Coordinate coordinate, RealmMap realmMap,
                  DynamicObjectSdb dynamicObjectSdb, String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName, parseAnimations(idName, dynamicObjectSdb, integer -> integer == 0 || integer == 2));
        fireLighted = 0;
    }

    @Override
    public DynamicObjectType type() {
        return DynamicObjectType.YAOHUA;
    }


    @Override
    public boolean canBeAttackedNow() {
        return currentLife() > 0 && getAnimationIndex() == 0
                && fireLighted >= 4;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent.source() instanceof TriggerDynamicObject triggerDynamicObject
            && triggerDynamicObject.idName().equals("狐狸火") &&
                entityEvent instanceof UpdateDynamicObjectEvent dynamicObjectEvent) {
            if (dynamicObjectEvent.frameStart() == 0) {
                fireLighted = fireLighted > 0 ? fireLighted - 1 : fireLighted;
            } else if (dynamicObjectEvent.frameEnd() == 19) {
                fireLighted = fireLighted <= 0 ? fireLighted + 1 : fireLighted;
            }
        }
    }

    @Override
    protected void onAnimationDone() {
        if (getAnimationIndex() == 2) {
            realmMap().free(this);
            emitEvent(new RemoveEntityEvent(this));
        } else if (getAnimationIndex() == 1) {
            changeAnimation(2, dynamicObjectSdb().getOpenedMillis(idName()));
        }
    }

    @Override
    boolean handleDamaged(Damage damage) {
        return doHandleDamaged(damage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Yaohua object)) return false;
        return Objects.equals(id(), object.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }
}
