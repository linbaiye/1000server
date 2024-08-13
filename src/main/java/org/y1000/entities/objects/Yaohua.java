package org.y1000.entities.objects;

import lombok.Builder;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Damage;
import org.y1000.event.CrossRealmEvent;
import org.y1000.message.PlayerTextEvent;
import org.y1000.realm.RealmMap;
import org.y1000.realm.event.BroadcastSoundEvent;
import org.y1000.realm.event.BroadcastTextEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.RealmLetterEvent;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Yaohua extends AbstractKillableDynamicObject
        implements EventDrivenDynamicObject {

    private final Set<TriggerDynamicObject> fires;

    @Builder
    public Yaohua(long id, Coordinate coordinate, RealmMap realmMap,
                  DynamicObjectSdb dynamicObjectSdb, String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName, parseAnimations(idName, dynamicObjectSdb, integer -> integer == 0 || integer == 2));
        fires = new HashSet<>();
    }

    @Override
    public DynamicObjectType type() {
        return DynamicObjectType.YAOHUA;
    }


    @Override
    public boolean canBeAttackedNow() {
        return currentLife() > 0 && getAnimationIndex() == 0 &&
                fires.stream().filter(TriggerDynamicObject::isTriggered).count() == 4;

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
    void handleDamaged(Damage damage) {
        damageLife(damage);
        if (currentLife() <= 0) {
            emitEvent(new CrossRealmEvent(this, new BroadcastSoundEvent("8950")));
            emitEvent(new CrossRealmEvent(this, new RealmLetterEvent<>(1, "九尾狐酒母", "shift")));
            emitEvent(new CrossRealmEvent(this, new BroadcastTextEvent(null, PlayerTextEvent.TextType.NINE_TAIL_FOX_SHIFT, PlayerTextEvent.ColorType.SIX_GRADE)));
            changeAnimation(1);
        } else {
            dynamicObjectSdb().getSoundSpecial(idName()).ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        }
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

    @Override
    public void subscribe(Set<DynamicObject> dynamicObjects) {
        if (dynamicObjects == null) {
            return;
        }
        dynamicObjects.stream().filter(dynamicObject -> dynamicObject.idName().equals("狐狸火"))
                .map(TriggerDynamicObject.class::cast)
                .forEach(fires::add);
    }
}
