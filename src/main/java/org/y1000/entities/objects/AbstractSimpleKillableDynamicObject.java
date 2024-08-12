package org.y1000.entities.objects;

import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Damage;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

public abstract class AbstractSimpleKillableDynamicObject extends AbstractKillableDynamicObject {
    public AbstractSimpleKillableDynamicObject(long id, Coordinate coordinate, RealmMap realmMap,
                                               DynamicObjectSdb dynamicObjectSdb,
                                               String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName, parseAnimationFrames(idName, dynamicObjectSdb));
    }
    void handleDamaged(Damage damage) {
        damageLife(damage);
        if (currentLife() == 0) {
            changeAnimation(1, dynamicObjectSdb().getOpenedMillis(idName()));
            dynamicObjectSdb().getSoundDie(idName())
                    .or(() -> dynamicObjectSdb().getSoundEvent(idName()))
                    .ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
            emitEvent(new DynamicObjectDieEvent(this));
        } else {
            dynamicObjectSdb().getSoundSpecial(idName()).ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        }
    }
    @Override
    public DynamicObjectType type() {
        return DynamicObjectType.KILLABLE;
    }

    @Override
    protected void onAnimationDone() {
        realmMap().free(this);
        emitEvent(new RemoveEntityEvent(this));
    }
}
